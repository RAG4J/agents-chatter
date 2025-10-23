package org.rag4j.chatter.web.agents;

import org.rag4j.chatter.web.messages.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class EchoAgent extends SubscriberAgent {
    private static final Logger logger = LoggerFactory.getLogger(EchoAgent.class);

    public static final String AGENT_NAME = "Echo Agent";

    private Disposable subscription;

    public EchoAgent(MessageService messageService) {
        super(AGENT_NAME, messageService);
    }


    @Override
    Logger logger() {
        return logger;
    }

    @Override
    Mono<String> messagePayload(String incomingPayload) {
        // Put a short timeout here to simulate processing delay

        return Mono.just("echo " + incomingPayload).delayElement(Duration.ofMillis(100));
    }

}
