package org.rag4j.chatter.core.moderation;

/**
 * Evaluates prospective agent messages and decides whether they should be published,
 * potentially rewriting the payload or rejecting it with a rationale.
 */
public interface ModeratorService {

    ModerationDecision evaluate(AgentMessageContext context);
}
