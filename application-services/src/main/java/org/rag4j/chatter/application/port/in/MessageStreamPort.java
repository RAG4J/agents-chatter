package org.rag4j.chatter.application.port.in;

import java.util.List;

import org.rag4j.chatter.domain.message.MessageEnvelope;

/**
 * Inbound port exposing the conversation message stream without leaking framework types.
 */
public interface MessageStreamPort {

    /**
     * Returns the current message history snapshot.
     */
    List<MessageEnvelope> history();

    /**
     * Subscribe to future conversation messages.
     */
    MessageStreamSubscription subscribe(MessageStreamSubscriber subscriber);

    @FunctionalInterface
    interface MessageStreamSubscriber {
        void onMessage(MessageEnvelope envelope);
    }

    interface MessageStreamSubscription extends AutoCloseable {

        @Override
        void close();

        default boolean isActive() {
            return true;
        }
    }
}
