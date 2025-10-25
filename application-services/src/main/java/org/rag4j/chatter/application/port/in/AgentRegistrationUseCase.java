package org.rag4j.chatter.application.port.in;

import org.rag4j.chatter.domain.agent.AgentDescriptor;

public interface AgentRegistrationUseCase {

    void register(AgentDescriptor descriptor);

    void unregister(String agentName);
}
