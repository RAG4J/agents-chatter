package org.rag4j.chatter.web.agents;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.rag4j.chatter.domain.message.MessageEnvelope;
import org.rag4j.chatter.web.messages.MessageService;
import org.rag4j.chatter.web.presence.PresenceRole;
import org.rag4j.chatter.web.presence.PresenceService;
import org.slf4j.Logger;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;

public abstract class SubscriberAgent {

    public static final String NO_MESSAGE_PLACEHOLDER = "#nothingtosay#";

    private final MessageService messageService;
    private final AgentPublisher agentPublisher;
    private final PresenceService presenceService;
    private final String agentName;
    private final PresenceRole role;
    private Disposable subscription;

    public SubscriberAgent(String agentName,
            PresenceRole role,
            MessageService messageService,
            AgentPublisher agentPublisher,
            PresenceService presenceService) {
        this.messageService = messageService;
        this.agentPublisher = agentPublisher;
        this.presenceService = presenceService;
        this.agentName = agentName;
        this.role = role;
    }

    @PostConstruct
    public void subscribe() {
        logger().info("Registering {} subscriber", agentName);
        if (presenceService != null) {
            presenceService.markOnline(agentName, role);
        }
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
        if (presenceService != null) {
            presenceService.markOffline(agentName);
        }
    }

    protected void publishResponse(MessageEnvelope incoming) {
        messagePayload(incoming)
                .map(responsePayload -> responsePayload == null ? "" : responsePayload.trim())
                .flatMap(responsePayload -> {
                    if (responsePayload.isBlank()) {
                        logger().info("Skipping empty response for {}", agentName);
                        return Mono.empty();
                    }
                    if (responsePayload.toLowerCase().contains(NO_MESSAGE_PLACEHOLDER)) {
                        logger().info("Suppressing placeholder response for {}", agentName);
                        return Mono.empty();
                    }
                    logger().info("Handling message from {} as '{}'", incoming.author(), responsePayload);
                    return agentPublisher.publishAgentResponse(agentName, responsePayload, incoming)
                            .doOnNext(envelope -> logger().info("Published agent response {} for thread {}",
                                    envelope.id(), envelope.threadId()))
                            .switchIfEmpty(Mono.fromRunnable(() -> logger().info(
                                    "Moderator rejected {} response on thread {}", agentName, incoming.threadId()))
                                    .then(Mono.empty()))
                            .then();
                })
                .subscribe();
    }

    protected boolean isOwnMessage(MessageEnvelope envelope) {
        logger().info("isOwnMessage: {}", agentName.equalsIgnoreCase(envelope.author()));
        return EchoAgent.AGENT_NAME.equalsIgnoreCase(envelope.author()) || agentName.equalsIgnoreCase(envelope.author());
    }

    abstract Logger logger();

    protected Mono<String> messagePayload(MessageEnvelope incoming) {
        return messagePayload(incoming.payload());
    }

    abstract Mono<String> messagePayload(String incomingPayload);

}
