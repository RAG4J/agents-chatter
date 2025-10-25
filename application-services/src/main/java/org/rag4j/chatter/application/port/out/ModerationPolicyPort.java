package org.rag4j.chatter.application.port.out;

import org.rag4j.chatter.domain.moderation.AgentMessageContext;
import org.rag4j.chatter.domain.moderation.ModerationDecision;

/**
 * Outbound port that abstracts moderation rule evaluation.
 * Implementations may call internal heuristics, external services, or AI models.
 */
public interface ModerationPolicyPort {

    /**
     * Evaluate the proposed agent message and return the moderation decision that should be
     * applied before publication.
     */
    ModerationDecision evaluate(AgentMessageContext context);
}
