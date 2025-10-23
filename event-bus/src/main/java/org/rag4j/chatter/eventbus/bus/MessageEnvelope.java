package org.rag4j.chatter.eventbus.bus;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Minimal message representation shared between publishers and subscribers.
 * Can be replaced by richer domain objects once the chat-domain module lands.
 */
public record MessageEnvelope(
        UUID id,
        String author,
        String payload,
        Instant createdAt) {

    public MessageEnvelope {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(author, "author must not be null");
        Objects.requireNonNull(payload, "payload must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
    }

    public static MessageEnvelope from(String author, String payload) {
        return new MessageEnvelope(UUID.randomUUID(), author, payload, Instant.now());
    }
}
