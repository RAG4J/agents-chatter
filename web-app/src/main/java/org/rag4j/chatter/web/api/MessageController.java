package org.rag4j.chatter.web.api;

import java.time.Duration;
import java.util.Map;

import org.rag4j.chatter.eventbus.bus.MessageBus;
import org.rag4j.chatter.eventbus.bus.MessageEnvelope;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Example REST controller demonstrating how to use the MessageBus from event-bus module.
 * 
 * Endpoints:
 * - POST /api/messages - Publish a message to the bus
 * - GET /api/messages/stream - Subscribe to message stream via Server-Sent Events
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageBus messageBus;

    public MessageController(MessageBus messageBus) {
        this.messageBus = messageBus;
    }

    /**
     * Publish a new message to the message bus.
     * Request body example: {"author": "alice", "payload": "Hello world"}
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> publishMessage(@RequestBody MessageRequest request) {
        var envelope = MessageEnvelope.from(request.author(), request.payload());
        boolean published = messageBus.publish(envelope);
        
        return Mono.just(Map.of(
            "published", published,
            "messageId", envelope.id().toString(),
            "timestamp", envelope.createdAt().toString()
        ));
    }

    /**
     * Subscribe to the message stream via Server-Sent Events.
     * Each subscriber receives messages published after they connect (hot stream).
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<MessageEvent>> streamMessages() {
        return messageBus.stream()
            .map(envelope -> ServerSentEvent.<MessageEvent>builder()
                .id(envelope.id().toString())
                .event("message")
                .data(new MessageEvent(
                    envelope.id().toString(),
                    envelope.author(),
                    envelope.payload(),
                    envelope.createdAt().toString()
                ))
                .build())
            .timeout(Duration.ofHours(1));
    }

    // DTOs
    public record MessageRequest(String author, String payload) {}
    
    public record MessageEvent(
        String id,
        String author,
        String payload,
        String timestamp
    ) {}
}
