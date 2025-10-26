package org.rag4j.chatter.web.agents;

import jakarta.annotation.PreDestroy;
import org.rag4j.chatter.eventbus.bus.MessageEnvelope;
import org.rag4j.chatter.web.messages.MessageService;
import org.rag4j.chatter.web.presence.PresenceRole;
import org.rag4j.chatter.web.presence.PresenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central service managing the lifecycle and infrastructure concerns for all agents.
 * 
 * <p>This manager handles:
 * <ul>
 *   <li>Agent subscription to message streams</li>
 *   <li>Lifecycle management (startup, shutdown)</li>
 *   <li>Presence tracking (online/offline)</li>
 *   <li>Activation state enforcement</li>
 *   <li>Message filtering (self-messages, empty responses)</li>
 *   <li>Response publication</li>
 * </ul>
 * 
 * <p>Agents implementing the {@link Agent} interface focus solely on business logic,
 * while this manager handles all cross-cutting concerns.
 */
@Service
public class AgentLifecycleManager {

    private static final Logger logger = LoggerFactory.getLogger(AgentLifecycleManager.class);

    private final MessageService messageService;
    private final AgentPublisher agentPublisher;
    private final PresenceService presenceService;
    private final AgentRegistry agentRegistry;
    private final Map<String, Disposable> subscriptions = new ConcurrentHashMap<>();

    public AgentLifecycleManager(
            MessageService messageService,
            AgentPublisher agentPublisher,
            PresenceService presenceService,
            AgentRegistry agentRegistry) {
        this.messageService = messageService;
        this.agentPublisher = agentPublisher;
        this.presenceService = presenceService;
        this.agentRegistry = agentRegistry;
    }

    /**
     * Subscribes an agent to the message stream and manages its lifecycle.
     * The agent will start receiving messages and can publish responses.
     * 
     * @param agent the agent to subscribe
     */
    public void subscribeAgent(Agent agent) {
        String agentName = agent.name();
        
        if (subscriptions.containsKey(agentName)) {
            logger.warn("Agent '{}' is already subscribed, skipping", agentName);
            return;
        }

        logger.info("Subscribing agent '{}'", agentName);

        // Register with activation registry
        agentRegistry.register(agentName, true);

        // Mark as online in presence system
        if (presenceService != null) {
            presenceService.markOnline(agentName, PresenceRole.AGENT);
        }

        // Subscribe to message stream
        Disposable subscription = messageService.stream()
                .filter(envelope -> shouldProcessMessage(agent, envelope))
                .subscribe(
                        envelope -> handleMessage(agent, envelope),
                        error -> logger.error("Agent '{}' stream error: {}", agentName, error.getMessage(), error)
                );

        subscriptions.put(agentName, subscription);
        logger.info("Agent '{}' successfully subscribed", agentName);
    }

    /**
     * Unsubscribes an agent and cleans up its resources.
     * 
     * @param agent the agent to unsubscribe
     */
    public void unsubscribeAgent(Agent agent) {
        String agentName = agent.name();
        
        Disposable subscription = subscriptions.remove(agentName);
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
            logger.info("Agent '{}' unsubscribed", agentName);
        }

        // Mark as offline in presence system
        if (presenceService != null) {
            presenceService.markOffline(agentName);
        }
    }

    @PreDestroy
    public void shutdown() {
        logger.info("Shutting down AgentLifecycleManager, disposing {} subscriptions", subscriptions.size());
        subscriptions.values().forEach(Disposable::dispose);
        subscriptions.clear();
    }

    /**
     * Determines if a message should be processed by the agent.
     */
    private boolean shouldProcessMessage(Agent agent, MessageEnvelope envelope) {
        String agentName = agent.name();

        // Skip messages from self
        if (isSelfMessage(agentName, envelope)) {
            logger.debug("Agent '{}' skipping own message", agentName);
            return false;
        }

        // Skip if agent is inactive
        if (!agentRegistry.isActive(agentName)) {
            logger.debug("Agent '{}' is inactive, skipping message", agentName);
            return false;
        }

        return true;
    }

    /**
     * Checks if the message is from the agent itself.
     */
    private boolean isSelfMessage(String agentName, MessageEnvelope envelope) {
        return agentName.equalsIgnoreCase(envelope.author());
    }

    /**
     * Handles a message by delegating to the agent and publishing the response.
     */
    private void handleMessage(Agent agent, MessageEnvelope envelope) {
        String agentName = agent.name();
        
        logger.debug("Agent '{}' processing message from '{}'", agentName, envelope.author());

        agent.processMessage(envelope)
                .map(response -> response == null ? "" : response.trim())
                .flatMap(response -> {
                    // Skip empty responses
                    if (response.isBlank()) {
                        logger.debug("Agent '{}' returned empty response, skipping", agentName);
                        return Mono.empty();
                    }

                    // Skip placeholder responses
                    if (response.toLowerCase().contains(Agent.NO_MESSAGE_PLACEHOLDER)) {
                        logger.debug("Agent '{}' returned placeholder response, skipping", agentName);
                        return Mono.empty();
                    }

                    logger.info("Agent '{}' publishing response: '{}'", agentName, 
                            response.length() > 50 ? response.substring(0, 50) + "..." : response);

                    // Publish the response
                    return agentPublisher.publishAgentResponse(agentName, response, envelope)
                            .doOnNext(published -> logger.info(
                                    "Agent '{}' response published: id={}, thread={}",
                                    agentName, published.id(), published.threadId()))
                            .switchIfEmpty(Mono.fromRunnable(() -> logger.info(
                                    "Agent '{}' response rejected by moderator for thread {}",
                                    agentName, envelope.threadId())))
                            .then();
                })
                .onErrorResume(error -> {
                    logger.error("Agent '{}' failed to process message: {}", 
                            agentName, error.getMessage(), error);
                    return Mono.empty();
                })
                .subscribe();
    }
}
