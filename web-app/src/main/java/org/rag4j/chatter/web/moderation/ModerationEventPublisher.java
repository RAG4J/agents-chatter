package org.rag4j.chatter.web.moderation;

import org.rag4j.chatter.domain.moderation.ModerationEvent;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class ModerationEventPublisher {

    private final Sinks.Many<ModerationEvent> sink = Sinks.many().multicast().onBackpressureBuffer();

    public void publish(ModerationEvent event) {
        sink.tryEmitNext(event);
    }

    public Flux<ModerationEvent> stream() {
        return sink.asFlux();
    }
}
