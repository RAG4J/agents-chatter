package org.rag4j.chatter.application.port.in.conversation;

/**
 * Inbound port allowing agents to submit messages back into the system via application services.
 */
public interface AgentMessagingCallback {

    PublishResult publish(PublishCommand command);
}
