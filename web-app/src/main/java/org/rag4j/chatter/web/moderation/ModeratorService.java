package org.rag4j.chatter.web.moderation;

import org.rag4j.chatter.application.port.out.ModerationPolicyPort;
import org.rag4j.chatter.domain.moderation.AgentMessageContext;
import org.rag4j.chatter.domain.moderation.ModerationDecision;

/**
 * Evaluates prospective agent messages and decides whether they should be published,
 * potentially rewriting the payload or rejecting it with a rationale.
 */
public interface ModeratorService extends ModerationPolicyPort {

    @Override
    ModerationDecision evaluate(AgentMessageContext context);
}
