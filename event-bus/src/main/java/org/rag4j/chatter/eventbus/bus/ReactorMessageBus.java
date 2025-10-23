package org.rag4j.chatter.eventbus.bus;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * Message bus backed by a Reactor {@link Sinks.Many} instance.
 */
public class ReactorMessageBus implements MessageBus {

    private static final Logger logger = LoggerFactory.getLogger(ReactorMessageBus.class);

    private final Sinks.Many<MessageEnvelope> sink;
    private final Flux<MessageEnvelope> sharedFlux;

    public ReactorMessageBus() {
        this(Sinks.many().multicast().directAllOrNothing());
    }

    public ReactorMessageBus(Sinks.Many<MessageEnvelope> sink) {
        this.sink = Objects.requireNonNull(sink, "sink must not be null");
        this.sharedFlux = this.sink.asFlux();
    }

    @Override
    public boolean publish(MessageEnvelope message) {
        Assert.notNull(message, "message must not be null");
        var result = sink.tryEmitNext(message);
        if (result.isFailure()) {
            logger.debug("Failed to emit message {} due to {}", message.id(), result);
        }
        return result.isSuccess();
    }

    @Override
    public Flux<MessageEnvelope> stream() {
        return sharedFlux;
    }
}
