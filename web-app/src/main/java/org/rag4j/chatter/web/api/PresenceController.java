package org.rag4j.chatter.web.api;

import java.util.List;

import org.rag4j.chatter.application.port.in.PresencePort;
import org.rag4j.chatter.domain.presence.PresenceParticipant;
import org.rag4j.chatter.domain.presence.PresenceRole;
import org.rag4j.chatter.domain.presence.PresenceStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

@RestController
@CrossOrigin
@RequestMapping("/api/presence")
public class PresenceController {

    private final PresencePort presencePort;

    public PresenceController(PresencePort presencePort) {
        this.presencePort = presencePort;
    }

    @GetMapping
    public List<PresenceDto> listPresence() {
        return presencePort.snapshot().stream().map(PresenceDto::from).toList();
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<PresenceDto>> streamPresence() {
        return Flux.<List<PresenceStatus>>create(emitter -> {
                    PresencePort.PresenceSubscription subscription = presencePort.subscribe(emitter::next);
                    emitter.onDispose(subscription::close);
                })
                .map(statuses -> statuses.stream().map(PresenceDto::from).toList());
    }

    public record PresenceDto(String name, PresenceRole role, boolean online, int connections) {
        public static PresenceDto from(PresenceStatus status) {
            PresenceParticipant participant = status.participant();
            return new PresenceDto(participant.name(), participant.role(), status.online(), status.connectionCount());
        }
    }
}
