package org.rag4j.chatter.domain.message;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Immutable representation of a chat message within the domain.
 */
public record MessageEnvelope(
        UUID id,
        String author,
        String payload,
        Instant createdAt,
        UUID threadId,
        Optional<UUID> parentMessageId,
        MessageOrigin originType,
        int agentReplyDepth) {

    public MessageEnvelope {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(author, "author must not be null");
        Objects.requireNonNull(payload, "payload must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(threadId, "threadId must not be null");
        Objects.requireNonNull(originType, "originType must not be null");
        if (agentReplyDepth < 0) {
            throw new IllegalArgumentException("agentReplyDepth must be >= 0");
        }
    }

    public static MessageEnvelope from(String author, String payload) {
        UUID messageId = UUID.randomUUID();
        return new MessageEnvelope(
                messageId,
                author,
                payload,
                Instant.now(),
                messageId,
                Optional.empty(),
                MessageOrigin.UNKNOWN,
                0);
    }

    public static MessageEnvelope fromMetadata(
            String author,
            String payload,
            UUID threadId,
            Optional<UUID> parentMessageId,
            MessageOrigin originType,
            int agentReplyDepth) {
        UUID messageId = UUID.randomUUID();
        return new MessageEnvelope(
                messageId,
                author,
                payload,
                Instant.now(),
                Objects.requireNonNull(threadId, "threadId must not be null"),
                parentMessageId,
                Objects.requireNonNull(originType, "originType must not be null"),
                agentReplyDepth);
    }

    public enum MessageOrigin {
        HUMAN,
        AGENT,
        UNKNOWN
    }
}
