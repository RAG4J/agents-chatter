package org.rag4j.chatter.domain.moderation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ModerationDecisionTests {

    @Test
    void approveWithPayloadWrapsValue() {
        ModerationDecision decision = ModerationDecision.approveWithPayload("updated");

        assertEquals(ModerationDecision.Status.APPROVE, decision.status());
        assertEquals("updated", decision.payloadOverride().orElseThrow());
    }

    @Test
    void rejectRequiresRationale() {
        assertThrows(IllegalArgumentException.class, () -> ModerationDecision.reject(""));
    }

    @Test
    void rejectionCreatesNonNullRationale() {
        ModerationDecision decision = ModerationDecision.reject("duplicate");

        assertEquals(ModerationDecision.Status.REJECT, decision.status());
        assertNotNull(decision.rationale());
    }
}
