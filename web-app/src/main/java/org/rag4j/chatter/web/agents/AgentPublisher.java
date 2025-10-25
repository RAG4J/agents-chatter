package org.rag4j.chatter.web.agents;

import java.util.Optional;
import java.util.UUID;

import org.rag4j.chatter.application.port.in.conversation.AgentMessagingCallback;
import org.rag4j.chatter.application.port.in.conversation.PublishCommand;
import org.rag4j.chatter.application.port.in.conversation.PublishResult;
import org.rag4j.chatter.domain.message.MessageEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

/**
 * Facade for agent publications that routes responses through the application-layer
 * {@link AgentMessagingCallback}, applying thread metadata and depth limits.
 */
@Component
public class AgentPublisher {

    private static final Logger logger = LoggerFactory.getLogger(AgentPublisher.class);

    private final AgentMessagingCallback agentMessaging;

    public AgentPublisher(AgentMessagingCallback agentMessaging) {
        this.agentMessaging = agentMessaging;
    }

    public Mono<MessageEnvelope> publishSpontaneousAgentMessage(String agentName, String payload) {
        return publishSpontaneousAgentMessage(agentName, payload, null);
    }

    public Mono<MessageEnvelope> publishAgentResponse(String agentName, String payload, MessageEnvelope parent) {
        return Mono.fromCallable(() -> agentMessaging.publish(
                        PublishCommand.forAgent(
                                agentName,
                                payload,
                                Optional.of(parent.threadId()),
                                Optional.of(parent.id()),
                                Optional.of(parent))))
                .flatMap(result -> {
                    if (result instanceof PublishResult.Accepted accepted) {
                        return Mono.just(accepted.envelope());
                    }
                    PublishResult.Rejected rejected = (PublishResult.Rejected) result;
                    logger.info("Agent {} response dropped for thread {} at depth {}: {}",
                            agentName,
                            rejected.threadId(),
                            rejected.attemptedDepth(),
                            rejected.reason());
                    return Mono.empty();
                })
                .onErrorResume(ex -> {
                    logger.warn("Agent {} failed to publish response: {}", agentName, ex.getMessage(), ex);
                    return Mono.empty();
                });
    }

    public Mono<MessageEnvelope> publishSpontaneousAgentMessage(String agentName, String payload, UUID threadId) {
        return Mono.fromCallable(() -> agentMessaging.publish(
                        PublishCommand.forAgent(
                                agentName,
                                payload,
                                Optional.ofNullable(threadId),
                                Optional.empty(),
                                Optional.empty())))
                .flatMap(result -> {
                    if (result instanceof PublishResult.Accepted accepted) {
                        return Mono.just(accepted.envelope());
                    }
                    return Mono.empty();
                })
                .onErrorResume(ex -> {
                    logger.warn("Agent {} failed to publish spontaneous message: {}", agentName, ex.getMessage(), ex);
                    return Mono.empty();
                });
    }
}
