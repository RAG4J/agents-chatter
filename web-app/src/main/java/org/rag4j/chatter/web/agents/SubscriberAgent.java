package org.rag4j.chatter.web.agents;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.rag4j.chatter.eventbus.bus.MessageEnvelope;
import org.rag4j.chatter.web.messages.MessageService;
import org.slf4j.Logger;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

public abstract class SubscriberAgent {

    private final MessageService messageService;
    private final String agentName;
    private Disposable subscription;

    public SubscriberAgent(String agentName, MessageService messageService) {
        this.messageService = messageService;
        this.agentName = agentName;
    }

    @PostConstruct
    public void subscribe() {
        logger().info("Registering {} subscriber", agentName);
        subscription = messageService.stream()
                .doOnNext(e -> logger().info("Received message from {}", e.author()))
                .filter(envelope -> !isOwnMessage(envelope))
                .doOnNext(this::publishResponse)
                .subscribe();
    }

    @PreDestroy
    public void shutdown() {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
        logger().info("{} subscriber disposed", agentName);
    }

    protected void publishResponse(MessageEnvelope incoming) {
        messagePayload(incoming.payload())
                .doOnNext(responsePayload -> logger().info("Handling message from {} as '{}'", incoming.author(),
                        responsePayload))
                .flatMap(responsePayload -> Mono.just(messageService.publish(agentName, responsePayload)))
                .subscribe();
    }

    protected boolean isOwnMessage(MessageEnvelope envelope) {
        logger().info("isOwnMessage: {}", agentName.equalsIgnoreCase(envelope.author()));
        return EchoAgent.AGENT_NAME.equalsIgnoreCase(envelope.author()) || agentName.equalsIgnoreCase(envelope.author());
    }

    abstract Logger logger();

    abstract Mono<String> messagePayload(String incomingPayload);

}
