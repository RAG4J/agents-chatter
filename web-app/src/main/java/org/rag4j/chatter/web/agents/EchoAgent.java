package org.rag4j.chatter.web.agents;

import java.time.Duration;

import org.rag4j.chatter.application.port.in.AgentMessageSubscriptionPort;
import org.rag4j.chatter.application.port.in.AgentRegistrationUseCase;
import org.rag4j.chatter.domain.message.MessageEnvelope;
import org.rag4j.chatter.domain.message.MessageEnvelope.MessageOrigin;
import org.rag4j.chatter.web.presence.PresenceRole;
import org.rag4j.chatter.web.presence.PresenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;

public class EchoAgent extends SubscriberAgent {
    private static final Logger logger = LoggerFactory.getLogger(EchoAgent.class);

    public static final String AGENT_NAME = "Echo Agent";

    private Disposable subscription;

    public EchoAgent(AgentMessageSubscriptionPort subscriptionPort,
            AgentPublisher agentPublisher,
            AgentRegistrationUseCase agentRegistry,
            PresenceService presenceService) {
        super(AGENT_NAME, PresenceRole.AGENT, subscriptionPort, agentPublisher, agentRegistry, presenceService);
    }


    @Override
    Logger logger() {
        return logger;
    }

    @Override
    protected Mono<String> messagePayload(MessageEnvelope incoming) {
        if (incoming.originType() == MessageOrigin.AGENT && incoming.agentReplyDepth() >= 2) {
            logger.info("Skipping echo due to agent depth {} on thread {}", incoming.agentReplyDepth(), incoming.threadId());
            return Mono.empty();
        }
        return super.messagePayload(incoming);
    }

    @Override
    Mono<String> messagePayload(String incomingPayload) {
        // Put a short timeout here to simulate processing delay

        return Mono.just("echo " + incomingPayload).delayElement(Duration.ofMillis(100));
    }

}
