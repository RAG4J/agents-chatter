package org.rag4j.chatter.application.port.out;

import java.util.List;

import org.rag4j.chatter.domain.agent.AgentDescriptor;

/**
 * Outbound port for managing agent registration and retrieval.
 */
public interface AgentRegistrationPort {

    void register(AgentDescriptor descriptor);

    void unregister(String agentName);

    List<AgentDescriptor> listActiveAgents();
}
