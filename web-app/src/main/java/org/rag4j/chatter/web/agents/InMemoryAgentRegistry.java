package org.rag4j.chatter.web.agents;

import org.rag4j.chatter.core.agent.AgentRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryAgentRegistry implements AgentRegistry {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryAgentRegistry.class);

    private final Map<String, AgentRegistration> agents = new ConcurrentHashMap<>();

    @Override
    public void register(String agentName, boolean initiallyActive) {
        String normalizedName = normalize(agentName);
        agents.put(normalizedName, new AgentRegistration(initiallyActive));
        logger.info("Registered agent '{}' with active={}", agentName, initiallyActive);
    }

    @Override
    public boolean isActive(String agentName) {
        AgentRegistration registration = agents.get(normalize(agentName));
        return registration != null && registration.isActive();
    }

    @Override
    public void setActive(String agentName, boolean active) {
        String normalizedName = normalize(agentName);
        AgentRegistration registration = agents.get(normalizedName);
        if (registration == null) {
            logger.warn("Attempted to set active state for unknown agent: {}", agentName);
            return;
        }
        registration.setActive(active);
        logger.info("Agent '{}' active state changed to {}", agentName, active);
    }

    @Override
    public boolean isRegistered(String agentName) {
        return agents.containsKey(normalize(agentName));
    }

    private static String normalize(String name) {
        return name == null ? "" : name.trim().toLowerCase(Locale.ENGLISH);
    }

    private static class AgentRegistration {
        private volatile boolean active;

        AgentRegistration(boolean active) {
            this.active = active;
        }

        synchronized boolean isActive() {
            return active;
        }

        synchronized void setActive(boolean active) {
            this.active = active;
        }
    }
}
