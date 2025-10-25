package org.rag4j.chatter.application.agents;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rag4j.chatter.application.port.out.AgentRegistrationPort;
import org.rag4j.chatter.domain.agent.AgentDescriptor;
import org.rag4j.chatter.domain.agent.AgentDescriptor.AgentType;

class AgentRegistryServiceTests {

    private RecordingRegistrationPort registrationPort;
    private AgentRegistryService registryService;

    @BeforeEach
    void setUp() {
        registrationPort = new RecordingRegistrationPort();
        registryService = new AgentRegistryService(registrationPort);
    }

    @Test
    void delegatesRegistrationToPort() {
        AgentDescriptor descriptor = new AgentDescriptor("echo", "Echo", AgentType.EMBEDDED, "local");

        registryService.register(descriptor);

        assertThat(registrationPort.registered).containsExactly(descriptor);
    }

    @Test
    void delegatesUnregister() {
        registryService.unregister("echo");

        assertThat(registrationPort.unregistered).containsExactly("echo");
    }

    @Test
    void retrievesActiveAgentsFromPort() {
        AgentDescriptor descriptor = new AgentDescriptor("remote", "Remote Agent", AgentType.REMOTE, "http://remote");
        registrationPort.registered.add(descriptor);

        List<AgentDescriptor> result = registryService.listAgents();

        assertThat(result).containsExactly(descriptor);
        assertThat(result).isNotSameAs(registrationPort.registered);
    }

    private static final class RecordingRegistrationPort implements AgentRegistrationPort {

        private final List<AgentDescriptor> registered = new ArrayList<>();
        private final List<String> unregistered = new ArrayList<>();

        @Override
        public void register(AgentDescriptor descriptor) {
            registered.add(descriptor);
        }

        @Override
        public void unregister(String agentName) {
            unregistered.add(agentName);
        }

        @Override
        public List<AgentDescriptor> listActiveAgents() {
            return new ArrayList<>(registered);
        }
    }
}
