package org.rag4j.chatter.web.moderation;

import org.rag4j.chatter.core.moderation.ModerationEvent;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/moderation/events")
@CrossOrigin
public class ModerationEventsController {

    private final ModerationEventPublisher eventPublisher;

    public ModerationEventsController(ModerationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ModerationEvent> stream() {
        return eventPublisher.stream();
    }

    @DeleteMapping
    public void clearEvents() {
        eventPublisher.clearEvents();
    }
}
