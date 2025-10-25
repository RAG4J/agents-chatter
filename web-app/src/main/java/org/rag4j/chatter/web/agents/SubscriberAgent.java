package org.rag4j.chatter.web.agents;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.rag4j.chatter.application.port.in.AgentMessageSubscriptionPort;
import org.rag4j.chatter.application.port.in.AgentRegistrationUseCase;
import org.rag4j.chatter.application.port.in.PresencePort;
import org.rag4j.chatter.domain.agent.AgentDescriptor;
import org.rag4j.chatter.domain.agent.AgentDescriptor.AgentType;
import org.rag4j.chatter.domain.message.MessageEnvelope;
import org.rag4j.chatter.domain.presence.PresenceRole;
import org.slf4j.Logger;

import reactor.core.publisher.Mono;

public abstract class SubscriberAgent {

    public static final String NO_MESSAGE_PLACEHOLDER = "#nothingtosay#";

    private final AgentMessageSubscriptionPort subscriptionPort;
    private final AgentPublisher agentPublisher;
    private final AgentRegistrationUseCase agentRegistry;
    private final PresencePort presencePort;
    private final String agentName;
    private final PresenceRole role;
    private final AgentDescriptor descriptor;
    private AgentMessageSubscriptionPort.AgentSubscription subscription;

    public SubscriberAgent(String agentName,
            PresenceRole role,
            AgentMessageSubscriptionPort subscriptionPort,
            AgentPublisher agentPublisher,
            AgentRegistrationUseCase agentRegistry,
            PresencePort presencePort) {
        this.subscriptionPort = subscriptionPort;
        this.agentPublisher = agentPublisher;
        this.agentRegistry = agentRegistry;
        this.presencePort = presencePort;
        this.agentName = agentName;
        this.role = role;
        this.descriptor = new AgentDescriptor(agentName, agentName, AgentType.EMBEDDED, "");
    }

    @PostConstruct
    public void subscribe() {
        logger().info("Registering {} subscriber", agentName);
        if (presencePort != null) {
            presencePort.markOnline(agentName, role);
        }
        if (agentRegistry != null) {
            agentRegistry.register(descriptor);
        }
        subscription = subscriptionPort.subscribe(descriptor, this::handleIncomingMessage);
    }

    @PreDestroy
    public void shutdown() {
        if (subscription != null) {
            subscription.close();
        }
        logger().info("{} subscriber disposed", agentName);
        if (presencePort != null) {
            presencePort.markOffline(agentName);
        }
        if (agentRegistry != null) {
            agentRegistry.unregister(agentName);
        }
    }

    private void handleIncomingMessage(MessageEnvelope envelope) {
        logger().info("Received message from {}", envelope.author());
        if (isOwnMessage(envelope)) {
            return;
        }
        publishResponse(envelope);
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
