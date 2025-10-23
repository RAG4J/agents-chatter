package org.rag4j.chatter.web.api;

import java.util.List;

import org.rag4j.chatter.web.presence.PresenceParticipant;
import org.rag4j.chatter.web.presence.PresenceRole;
import org.rag4j.chatter.web.presence.PresenceService;
import org.rag4j.chatter.web.presence.PresenceStatus;
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

    private final PresenceService presenceService;

    public PresenceController(PresenceService presenceService) {
        this.presenceService = presenceService;
    }

    @GetMapping
    public List<PresenceDto> listPresence() {
        return presenceService.snapshot().stream().map(PresenceDto::from).toList();
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<PresenceDto>> streamPresence() {
        return presenceService.stream().map(statuses -> statuses.stream().map(PresenceDto::from).toList());
    }

    public record PresenceDto(String name, PresenceRole role, boolean online, int connections) {
        public static PresenceDto from(PresenceStatus status) {
            PresenceParticipant participant = status.participant();
            return new PresenceDto(participant.name(), participant.role(), status.online(), status.connectionCount());
        }
    }
}
