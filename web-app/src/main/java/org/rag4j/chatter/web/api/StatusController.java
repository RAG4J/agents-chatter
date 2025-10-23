package org.rag4j.chatter.web.api;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/api/status", produces = MediaType.APPLICATION_JSON_VALUE)
public class StatusController {

    @GetMapping
    public Mono<Map<String, Object>> status() {
        return Mono.fromSupplier(() -> Map.of(
                "service", "agents-chatter-web",
                "status", "ok",
                "timestamp", Instant.now().toString()));
    }
}
