package org.rag4j.chatter.core.agent;

/**
 * Registry for managing agent activation status.
 */
public interface AgentRegistry {

    /**
     * Registers an agent with the given name and initial active state.
     *
     * @param agentName      the name of the agent
     * @param initiallyActive whether the agent is initially active
     */
    void register(String agentName, boolean initiallyActive);

    /**
     * Checks if the agent with the given name is currently active.
     *
     * @param agentName the name of the agent
     * @return true if the agent is active, false otherwise
     */
    boolean isActive(String agentName);

    /**
     * Sets the active state of the agent with the given name.
     *
     * @param agentName the name of the agent
     * @param active    the new active state
     */
    void setActive(String agentName, boolean active);

    /**
     * Checks if an agent with the given name is registered.
     *
     * @param agentName the name of the agent
     * @return true if the agent is registered, false otherwise
     */
    boolean isRegistered(String agentName);
}
