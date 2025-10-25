package org.rag4j.chatter.web.messages;

import org.rag4j.chatter.application.port.in.MessageStreamPort;
import org.rag4j.chatter.application.port.in.MessageStreamPort.MessageStreamSubscription;
import org.rag4j.chatter.domain.message.MessageEnvelope;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;

/**
 * Bridges the message stream port to Reactor for WebFlux consumers.
 */
@Component
public class MessageStreamFluxAdapter {

    private final MessageStreamPort messageStreamPort;

    public MessageStreamFluxAdapter(MessageStreamPort messageStreamPort) {
        this.messageStreamPort = messageStreamPort;
    }

    public Flux<MessageEnvelope> stream() {
        return Flux.create(emitter -> {
            MessageStreamSubscription subscription = messageStreamPort.subscribe(emitter::next);
            emitter.onDispose(subscription::close);
        });
    }
}
