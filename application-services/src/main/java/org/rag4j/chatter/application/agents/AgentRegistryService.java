package org.rag4j.chatter.application.agents;

import java.util.List;

import org.rag4j.chatter.application.port.in.AgentDiscoveryPort;
import org.rag4j.chatter.application.port.in.AgentRegistrationUseCase;
import org.rag4j.chatter.application.port.out.AgentRegistrationPort;
import org.rag4j.chatter.domain.agent.AgentDescriptor;

/**
 * Application service coordinating agent registration and discovery. Delegates storage concerns
 * to an outbound registration port.
 */
public class AgentRegistryService implements AgentRegistrationUseCase, AgentDiscoveryPort {

    private final AgentRegistrationPort registrationPort;

    public AgentRegistryService(AgentRegistrationPort registrationPort) {
        this.registrationPort = registrationPort;
    }

    @Override
    public void register(AgentDescriptor descriptor) {
        registrationPort.register(descriptor);
    }

    @Override
    public void unregister(String agentName) {
        registrationPort.unregister(agentName);
    }

    @Override
    public List<AgentDescriptor> listAgents() {
        return registrationPort.listActiveAgents();
    }
}
