package org.rag4j.chatter.web.agents;

import org.rag4j.chatter.application.port.in.AgentMessageSubscriptionPort;
import org.rag4j.chatter.application.port.in.MessageStreamPort;
import org.rag4j.chatter.domain.agent.AgentDescriptor;
import org.rag4j.chatter.domain.message.MessageEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Bridges the application-layer agent subscription port to the message stream port.
 */
@Component
public class MessageSubscriptionAdapter implements AgentMessageSubscriptionPort {

    private static final Logger logger = LoggerFactory.getLogger(MessageSubscriptionAdapter.class);

    private final MessageStreamPort messageStreamPort;

    public MessageSubscriptionAdapter(MessageStreamPort messageStreamPort) {
        this.messageStreamPort = messageStreamPort;
    }

    @Override
    public AgentSubscription subscribe(AgentDescriptor agent, AgentMessageSubscriber subscriber) {
        MessageStreamPort.MessageStreamSubscription subscription = messageStreamPort.subscribe(message -> {
            try {
                subscriber.onMessage(message);
            }
            catch (Exception ex) {
                logger.warn("Agent {} subscriber threw exception: {}", agent.name(), ex.getMessage(), ex);
            }
        });

        return new MessageStreamAgentSubscription(subscription);
    }

    private static final class MessageStreamAgentSubscription implements AgentSubscription {

        private final MessageStreamPort.MessageStreamSubscription delegate;

        private MessageStreamAgentSubscription(MessageStreamPort.MessageStreamSubscription delegate) {
            this.delegate = delegate;
        }

        @Override
        public void close() {
            delegate.close();
        }

        @Override
        public boolean isActive() {
            return delegate.isActive();
        }
    }
}
