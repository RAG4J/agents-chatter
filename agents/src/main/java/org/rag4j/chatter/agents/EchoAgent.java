package org.rag4j.chatter.agents;

import java.time.Duration;

import jakarta.annotation.PostConstruct;
import org.rag4j.chatter.core.agent.Agent;
import org.rag4j.chatter.core.agent.AgentLifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class EchoAgent implements Agent {
    private static final Logger logger = LoggerFactory.getLogger(EchoAgent.class);

    public static final String AGENT_NAME = "Echo Agent";

    private final AgentLifecycleManager lifecycleManager;

    public EchoAgent(AgentLifecycleManager lifecycleManager) {
        this.lifecycleManager = lifecycleManager;
    }

    @PostConstruct
    public void init() {
        lifecycleManager.subscribeAgent(this);
    }

    @Override
    public String name() {
        return AGENT_NAME;
    }

    @Override
    public Mono<String> processMessage(String payload) {
        logger.debug("EchoAgent processMessage: {}", payload);

        // Simple echo with simulated processing delay
        return Mono.just("echo " + payload).delayElement(Duration.ofMillis(100));
    }
}
