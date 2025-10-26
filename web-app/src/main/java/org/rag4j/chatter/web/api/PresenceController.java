package org.rag4j.chatter.web.api;

import java.util.List;

import org.rag4j.chatter.web.presence.PresenceParticipant;
import org.rag4j.chatter.web.presence.PresenceRole;
import org.rag4j.chatter.web.presence.PresenceService;
import org.rag4j.chatter.web.presence.PresenceStatus;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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

    @PostMapping("/{agentName}/activate")
    public PresenceDto activateAgent(@PathVariable("agentName") String agentName) {
        try {
            presenceService.setAgentActive(agentName, true);
            return findAgent(agentName);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{agentName}/deactivate")
    public PresenceDto deactivateAgent(@PathVariable("agentName") String agentName) {
        try {
            presenceService.setAgentActive(agentName, false);
            return findAgent(agentName);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private PresenceDto findAgent(String agentName) {
        return presenceService.snapshot().stream()
                .filter(status -> status.participant().name().equalsIgnoreCase(agentName))
                .findFirst()
                .map(PresenceDto::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agent not found: " + agentName));
    }

    public record PresenceDto(String name, PresenceRole role, boolean online, int connections, boolean active) {
        public static PresenceDto from(PresenceStatus status) {
            PresenceParticipant participant = status.participant();
            return new PresenceDto(participant.name(), participant.role(), status.online(), status.connectionCount(), status.active());
        }
    }
}
