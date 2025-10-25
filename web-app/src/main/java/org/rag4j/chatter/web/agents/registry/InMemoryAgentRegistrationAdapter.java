package org.rag4j.chatter.web.agents.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.rag4j.chatter.application.port.out.AgentRegistrationPort;
import org.rag4j.chatter.domain.agent.AgentDescriptor;
import org.springframework.stereotype.Component;

@Component
public class InMemoryAgentRegistrationAdapter implements AgentRegistrationPort {

    private final Map<String, AgentDescriptor> agents = new ConcurrentHashMap<>();

    @Override
    public void register(AgentDescriptor descriptor) {
        agents.put(descriptor.name(), descriptor);
    }

    @Override
    public void unregister(String agentName) {
        agents.remove(agentName);
    }

    @Override
    public List<AgentDescriptor> listActiveAgents() {
        return new ArrayList<>(agents.values());
    }
}
