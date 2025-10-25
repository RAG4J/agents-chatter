package org.rag4j.chatter.application.port.in.conversation;

import java.util.Optional;
import java.util.UUID;

import org.rag4j.chatter.domain.message.MessageEnvelope;
import org.rag4j.chatter.domain.message.MessageEnvelope.MessageOrigin;

/**
 * Command capturing the intent to publish a message. It is agnostic of delivery mechanism and
 * contains just enough context for the application service to apply policies and routing.
 */
public record PublishCommand(
        String author,
        String payload,
        MessageOrigin originType,
        Optional<UUID> threadId,
        Optional<UUID> parentMessageId,
        Optional<MessageEnvelope> parentEnvelope) {

    public PublishCommand {
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

    /**
     * Convenience factory for human-authored messages.
     */
    public static PublishCommand forHuman(String author, String payload, Optional<UUID> threadId) {
        return new PublishCommand(author, payload, MessageOrigin.HUMAN, threadId, Optional.empty(), Optional.empty());
    }

    /**
     * Convenience factory for agent-authored messages carrying parent context.
     */
    public static PublishCommand forAgent(
            String author,
            String payload,
            Optional<UUID> threadId,
            Optional<UUID> parentMessageId,
            Optional<MessageEnvelope> parentEnvelope) {
        return new PublishCommand(author, payload, MessageOrigin.AGENT, threadId, parentMessageId, parentEnvelope);
    }
}
