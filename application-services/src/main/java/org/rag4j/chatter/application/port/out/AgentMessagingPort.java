package org.rag4j.chatter.application.port.out;

import org.rag4j.chatter.domain.message.MessageEnvelope;

/**
 * Outbound port responsible for delivering messages to agents (embedded or external).
 */
public interface AgentMessagingPort {

    void deliver(MessageEnvelope envelope);
}
