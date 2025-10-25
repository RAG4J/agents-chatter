package org.rag4j.chatter.domain.moderation;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record ModerationEvent(
        UUID threadId,
        String agent,
        String rationale,
        Instant occurredAt,
        Optional<String> messagePreview,
        Optional<Integer> attemptedDepth) {

    public ModerationEvent {
        Objects.requireNonNull(threadId, "threadId must not be null");
        Objects.requireNonNull(agent, "agent must not be null");
        Objects.requireNonNull(rationale, "rationale must not be null");
        Objects.requireNonNull(occurredAt, "occurredAt must not be null");
    }

    public static ModerationEvent rejection(
            UUID threadId,
            String agent,
            String rationale,
            Instant occurredAt,
            Optional<String> messagePreview,
            Optional<Integer> attemptedDepth) {
        return new ModerationEvent(
                threadId,
                agent,
                rationale,
                occurredAt,
                messagePreview,
                attemptedDepth);
    }
}
