package org.rag4j.chatter.web.messages;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.rag4j.chatter.core.message.MessageEnvelope;
import org.rag4j.chatter.core.message.MessageEnvelope.MessageOrigin;
import org.rag4j.chatter.core.moderation.AgentMessageContext;
import org.rag4j.chatter.core.moderation.ModerationDecision;
import org.rag4j.chatter.core.moderation.ModerationEvent;
import org.rag4j.chatter.core.moderation.ModeratorService;
import org.rag4j.chatter.web.moderation.ModerationEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Coordinates message publication by enriching metadata (thread, parent, depth)
 * and enforcing agent reply depth limits before delegating to {@link MessageService}.
 */
@Component
public class ConversationCoordinator {

    private static final Logger logger = LoggerFactory.getLogger(ConversationCoordinator.class);

    private final MessageService messageService;
    private final int maxAgentDepth;
    private final ModeratorService moderatorService;
    private final ModerationEventPublisher eventPublisher;
    private final ConcurrentMap<UUID, ThreadState> threads = new ConcurrentHashMap<>();

    public ConversationCoordinator(
            MessageService messageService,
            @Value("${conversation.max-agent-depth:2}") int maxAgentDepth,
            ModeratorService moderatorService,
            ModerationEventPublisher eventPublisher) {
        if (maxAgentDepth < 1) {
            throw new IllegalArgumentException("conversation.max-agent-depth must be >= 1");
        }
        this.messageService = messageService;
        this.maxAgentDepth = maxAgentDepth;
        this.moderatorService = moderatorService;
        this.eventPublisher = eventPublisher;
    }

    public PublishResult handlePublish(PublishRequest request) {
        UUID threadId = request.threadId()
                .orElseGet(() -> {
                    if (request.originType() == MessageOrigin.AGENT) {
                        logger.warn("Agent {} attempted to publish without thread context; generating new thread id", request.author());
                    }
                    return UUID.randomUUID();
                });

        ThreadState currentState = threads.get(threadId);

        // Calculate depth based on parent message, not thread-wide counter
        int nextDepth;
        if (request.originType() == MessageOrigin.HUMAN) {
            nextDepth = 0;
        } else if (request.parentEnvelope().isPresent()) {
            // Agent replying to a specific message: inherit parent's depth + 1
            nextDepth = request.parentEnvelope().get().agentReplyDepth() + 1;
        } else {
            // Agent replying without parent context (spontaneous message)
            logger.warn("Agent {} publishing without parent envelope, defaulting to depth 1", request.author());
            nextDepth = 1;
        }

        if (request.originType() == MessageOrigin.AGENT && nextDepth > maxAgentDepth) {
            logger.info("Blocking agent response for {} due to depth {} > {}", request.author(), nextDepth, maxAgentDepth);
            eventPublisher.publish(ModerationEvent.rejection(
                    threadId,
                    request.author(),
                    "Agent reply depth exceeded maximum of " + maxAgentDepth,
                    Instant.now(),
                    Optional.ofNullable(request.payload()).map(String::trim).filter(s -> !s.isBlank()),
                    Optional.of(nextDepth)));
            return PublishResult.rejected(threadId, nextDepth,
                    "Agent reply depth exceeded maximum of " + maxAgentDepth);
        }

        Optional<UUID> parent = request.parentMessageId()
                .or(() -> Optional.ofNullable(currentState != null ? currentState.lastMessageId : null));

        String payload = request.payload();

        if (request.originType() == MessageOrigin.AGENT) {
            AgentMessageContext context = new AgentMessageContext(
                    request.author(),
                    request.payload(),
                    threadId,
                    parent,
                    nextDepth,
                    request.parentEnvelope());

            ModerationDecision decision = moderatorService.evaluate(context);
            if (decision.status() == ModerationDecision.Status.REJECT) {
                return PublishResult.rejected(threadId, nextDepth, decision.rationale());
            }
            payload = decision.payloadOverride().orElse(payload);
        }

        MessageEnvelope envelope = MessageEnvelope.fromMetadata(
                request.author(),
                payload,
                threadId,
                parent,
                request.originType(),
                nextDepth);

        MessageEnvelope published = messageService.publish(envelope);

        // Update thread state with last message (depth tracking is now per-message)
        threads.put(threadId, new ThreadState(published.id(), Instant.now()));

        return PublishResult.accepted(published);
    }

    public record PublishRequest(
            String author,
            String payload,
            MessageOrigin originType,
            Optional<UUID> threadId,
            Optional<UUID> parentMessageId,
            Optional<MessageEnvelope> parentEnvelope) {

        public PublishRequest {
            if (author == null || author.isBlank()) {
                throw new IllegalArgumentException("author must not be blank");
            }
            if (payload == null) {
                throw new IllegalArgumentException("payload must not be null");
            }
            originType = originType == null ? MessageOrigin.UNKNOWN : originType;
            threadId = threadId == null ? Optional.empty() : threadId;
            parentMessageId = parentMessageId == null ? Optional.empty() : parentMessageId;
            parentEnvelope = parentEnvelope == null ? Optional.empty() : parentEnvelope;
        }

        public static PublishRequest forHuman(String author, String payload, Optional<UUID> threadId) {
            return new PublishRequest(author, payload, MessageOrigin.HUMAN, threadId, Optional.empty(), Optional.empty());
        }

        public static PublishRequest forAgent(String author,
                String payload,
                Optional<UUID> threadId,
                Optional<UUID> parentMessageId,
                Optional<MessageEnvelope> parentEnvelope) {
            return new PublishRequest(author, payload, MessageOrigin.AGENT, threadId, parentMessageId, parentEnvelope);
        }
    }

    public sealed interface PublishResult permits PublishResult.Accepted, PublishResult.Rejected {

        static PublishResult accepted(MessageEnvelope envelope) {
            return new Accepted(envelope);
        }

        static PublishResult rejected(UUID threadId, int attemptedDepth, String reason) {
            return new Rejected(threadId, attemptedDepth, reason);
        }

        record Accepted(MessageEnvelope envelope) implements PublishResult {
        }

        record Rejected(UUID threadId, int attemptedDepth, String reason) implements PublishResult {
        }
    }

    /**
     * Tracks the last message ID in a thread for parent linking.
     * Depth is now calculated from the parent message, not stored here.
     */
    private record ThreadState(UUID lastMessageId, Instant updatedAt) {
    }
}
