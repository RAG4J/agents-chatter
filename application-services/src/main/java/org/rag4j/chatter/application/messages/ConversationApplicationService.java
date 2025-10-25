package org.rag4j.chatter.application.messages;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.rag4j.chatter.application.port.in.AgentMessagingCallback;
import org.rag4j.chatter.application.port.out.MessagePublicationPort;
import org.rag4j.chatter.application.port.out.ModerationEventPort;
import org.rag4j.chatter.application.port.out.ModerationPolicyPort;
import org.rag4j.chatter.domain.message.MessageEnvelope;
import org.rag4j.chatter.domain.message.MessageEnvelope.MessageOrigin;
import org.rag4j.chatter.domain.moderation.AgentMessageContext;
import org.rag4j.chatter.domain.moderation.ModerationDecision;
import org.rag4j.chatter.domain.moderation.ModerationEvent;

/**
 * Application service responsible for orchestrating message publication. It enforces depth limits,
 * invokes moderation rules, and dispatches moderation telemetry while remaining framework-free.
 */
public class ConversationApplicationService implements AgentMessagingCallback {

    private final MessagePublicationPort messagePublicationPort;
    private final ModerationPolicyPort moderationPolicyPort;
    private final ModerationEventPort moderationEventPort;
    private final int maxAgentDepth;
    private final ConcurrentMap<UUID, ThreadState> threads = new ConcurrentHashMap<>();

    public ConversationApplicationService(
            MessagePublicationPort messagePublicationPort,
            ModerationPolicyPort moderationPolicyPort,
            ModerationEventPort moderationEventPort,
            int maxAgentDepth) {
        this.messagePublicationPort = messagePublicationPort;
        this.moderationPolicyPort = moderationPolicyPort;
        this.moderationEventPort = moderationEventPort;
        if (maxAgentDepth < 1) {
            throw new IllegalArgumentException("maxAgentDepth must be >= 1");
        }
        this.maxAgentDepth = maxAgentDepth;
    }

    /**
     * Execute the publish use case for the supplied command.
     */
    @Override
    public PublishResult publish(PublishCommand command) {
        UUID threadId = command.threadId()
                .orElseGet(() -> UUID.randomUUID());

        ThreadState currentState = threads.get(threadId);

        int nextDepth = command.originType() == MessageOrigin.AGENT
                ? ((currentState != null ? currentState.agentDepth : 0) + 1)
                : 0;

        if (command.originType() == MessageOrigin.AGENT && nextDepth > maxAgentDepth) {
            moderationEventPort.publish(ModerationEvent.rejection(
                    threadId,
                    command.author(),
                    "Agent reply depth exceeded maximum of " + maxAgentDepth,
                    Instant.now(),
                    Optional.of(command.payload()).map(String::trim).filter(s -> !s.isBlank()),
                    Optional.of(nextDepth)));
            return PublishResult.rejected(threadId, nextDepth, "Agent reply depth exceeded maximum of " + maxAgentDepth);
        }

        Optional<UUID> parent = command.parentMessageId()
                .or(() -> Optional.ofNullable(currentState != null ? currentState.lastMessageId : null));

        String payload = command.payload();

        if (command.originType() == MessageOrigin.AGENT) {
            AgentMessageContext context = new AgentMessageContext(
                    command.author(),
                    command.payload(),
                    threadId,
                    parent,
                    nextDepth,
                    command.parentEnvelope());

            ModerationDecision decision = moderationPolicyPort.evaluate(context);
            if (decision.status() == ModerationDecision.Status.REJECT) {
                return PublishResult.rejected(threadId, nextDepth, decision.rationale());
            }
            payload = decision.payloadOverride().orElse(payload);
        }

        MessageEnvelope envelope = MessageEnvelope.fromMetadata(
                command.author(),
                payload,
                threadId,
                parent,
                command.originType(),
                nextDepth);

        MessageEnvelope published = messagePublicationPort.publish(envelope);

        if (command.originType() == MessageOrigin.HUMAN) {
            threads.put(threadId, ThreadState.forHuman(published.id()));
        } else {
            threads.put(threadId, ThreadState.forAgent(published.id(), nextDepth));
        }

        return PublishResult.accepted(published);
    }

    private record ThreadState(UUID lastMessageId, int agentDepth) {
        static ThreadState forHuman(UUID messageId) {
            return new ThreadState(messageId, 0);
        }

        static ThreadState forAgent(UUID messageId, int depth) {
            return new ThreadState(messageId, depth);
        }
    }
}
