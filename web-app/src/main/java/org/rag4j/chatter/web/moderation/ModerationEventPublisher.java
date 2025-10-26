package org.rag4j.chatter.web.moderation;

import org.rag4j.chatter.core.moderation.ModerationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class ModerationEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(ModerationEventPublisher.class);
    // Use replay to keep sink alive even when no subscribers are connected
    private final Sinks.Many<ModerationEvent> sink = Sinks.many().replay().limit(10);

    public void publish(ModerationEvent event) {
        logger.info("Publishing moderation event: agent={}, threadId={}, rationale={}", 
                event.agent(), event.threadId(), event.rationale());
        Sinks.EmitResult result = sink.tryEmitNext(event);
        logger.info("Emit result: {}", result);
    }

    public Flux<ModerationEvent> stream() {
        return sink.asFlux();
    }

    public void clearEvents() {
        logger.info("Clearing moderation events");
        // Note: The replay sink retains history, but we can't clear it directly.
        // Events will naturally age out as new events are added (limited to 10).
        // For now, this is a no-op placeholder for consistency with the API.
    }
}
