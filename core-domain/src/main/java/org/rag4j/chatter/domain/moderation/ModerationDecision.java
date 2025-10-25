package org.rag4j.chatter.domain.moderation;

import java.util.Optional;

public record ModerationDecision(Status status, Optional<String> payloadOverride, String rationale) {

    public enum Status {
        APPROVE,
        REJECT
    }

    public static ModerationDecision approve() {
        return new ModerationDecision(Status.APPROVE, Optional.empty(), null);
    }

    public static ModerationDecision approveWithPayload(String payload) {
        return new ModerationDecision(Status.APPROVE, Optional.ofNullable(payload), null);
    }

    public static ModerationDecision reject(String rationale) {
        if (rationale == null || rationale.isBlank()) {
            throw new IllegalArgumentException("rationale must be provided when rejecting");
        }
        return new ModerationDecision(Status.REJECT, Optional.empty(), rationale);
    }
}
