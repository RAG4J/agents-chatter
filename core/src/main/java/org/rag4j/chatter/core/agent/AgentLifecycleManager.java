package org.rag4j.chatter.core.agent;

public interface AgentLifecycleManager {
    void subscribeAgent(Agent agent);

    void unsubscribeAgent(Agent agent);
}
