package org.rag4j.chatter.web.agents;

import org.rag4j.chatter.application.port.in.AgentMessageSubscriptionPort;
import org.rag4j.chatter.domain.agent.AgentDescriptor;
import org.rag4j.chatter.domain.message.MessageEnvelope;
import org.rag4j.chatter.web.messages.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;

/**
 * Bridges the application-layer agent subscription port to the existing Reactor-based
 * {@link MessageService} stream.
 */
@Component
public class MessageSubscriptionAdapter implements AgentMessageSubscriptionPort {

    private static final Logger logger = LoggerFactory.getLogger(MessageSubscriptionAdapter.class);

    private final MessageService messageService;

    public MessageSubscriptionAdapter(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public AgentSubscription subscribe(AgentDescriptor agent, AgentMessageSubscriber subscriber) {
        Flux<MessageEnvelope> stream = messageService.stream()
                .doOnError(error -> logger.warn("Agent {} stream encountered error: {}", agent.name(), error.getMessage(), error));

        Disposable disposable = stream.subscribe(
                subscriber::onMessage,
                error -> logger.debug("Agent {} subscriber terminated due to error", agent.name(), error),
                () -> logger.debug("Agent {} subscriber completed", agent.name()));

        return new DisposableAgentSubscription(disposable);
    }

    private static final class DisposableAgentSubscription implements AgentSubscription {

        private final Disposable delegate;

        private DisposableAgentSubscription(Disposable delegate) {
            this.delegate = delegate;
        }

        @Override
        public void close() {
            if (!delegate.isDisposed()) {
                delegate.dispose();
            }
        }

        @Override
        public boolean isActive() {
            return !delegate.isDisposed();
        }
    }
}
