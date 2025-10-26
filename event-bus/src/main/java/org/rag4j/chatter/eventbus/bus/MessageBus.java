package org.rag4j.chatter.eventbus.bus;

import org.rag4j.chatter.core.message.MessageEnvelope;
import reactor.core.publisher.Flux;

/**
 * Contract for publishing chat messages and subscribing to the shared stream.
 */
public interface MessageBus {

    /**
     * Publish a new message to all subscribers.
     * @param message message to dispatch
     * @return {@code true} when the message was accepted by the underlying sink
     */
    boolean publish(MessageEnvelope message);

    /**
     * Provides a hot {@link Flux} for subscribers; each subscription receives messages
     * emitted after it subscribed.
     * @return shared message stream
     */
    Flux<MessageEnvelope> stream();
}
