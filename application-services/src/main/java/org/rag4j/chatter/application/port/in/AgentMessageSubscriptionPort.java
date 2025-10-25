package org.rag4j.chatter.application.port.in;

import org.rag4j.chatter.domain.agent.AgentDescriptor;
import org.rag4j.chatter.domain.message.MessageEnvelope;

/**
 * Inbound port that allows agent runtimes to subscribe to conversation activity without
 * depending on framework-specific streaming types.
 */
public interface AgentMessageSubscriptionPort {

    /**
     * Register the supplied subscriber and begin delivering conversation messages that the
     * agent should react to.
     *
     * @param agent         descriptor describing the agent that is subscribing
     * @param subscriber    callback that will receive incoming messages
     * @return handle that allows callers to cancel the subscription when the agent shuts down
     */
    AgentSubscription subscribe(AgentDescriptor agent, AgentMessageSubscriber subscriber);

    @FunctionalInterface
    interface AgentMessageSubscriber {

        /**
         * Called for each message that the agent should process.
         */
        void onMessage(MessageEnvelope envelope);
    }

    /**
     * Represents the lifecycle of an active agent subscription.
     */
    interface AgentSubscription extends AutoCloseable {

        /**
         * Cancel the subscription and stop delivering messages.
         */
        @Override
        void close();

        /**
         * Indicates whether the subscription is still active.
         */
        default boolean isActive() {
            return true;
        }
    }
}
