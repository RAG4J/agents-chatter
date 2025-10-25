package org.rag4j.chatter.application.agents;

import org.rag4j.chatter.application.port.in.AgentDiscoveryPort;
import org.rag4j.chatter.application.port.in.AgentRegistrationUseCase;
import org.rag4j.chatter.application.port.out.AgentRegistrationPort;

public final class AgentRegistryServices {

    private AgentRegistryServices() {
    }

    public static AgentRegistryUseCases create(AgentRegistrationPort port) {
        AgentRegistryService service = new AgentRegistryService(port);
        return new AgentRegistryUseCases(service, service);
    }

    public record AgentRegistryUseCases(
            AgentRegistrationUseCase registrationUseCase,
            AgentDiscoveryPort discoveryPort) {
    }
}
