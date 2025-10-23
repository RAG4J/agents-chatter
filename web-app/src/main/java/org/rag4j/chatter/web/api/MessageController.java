package org.rag4j.chatter.web.api;

import java.util.List;

import org.rag4j.chatter.web.messages.MessageDto;
import org.rag4j.chatter.web.messages.MessageService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

/** REST endpoints for chat message retrieval and publishing. */
@RestController
@RequestMapping("/api/messages")
@CrossOrigin
@Validated
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<MessageDto>> getMessages() {
        return Mono.fromSupplier(() -> messageService.getHistory().stream()
            .map(MessageDto::from)
            .toList());
    }

    /**
     * Publish a new message to the message bus.
     * Request body example: {"author": "alice", "payload": "Hello world"}
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<MessageDto> publishMessage(@RequestBody MessageRequest request) {
        return Mono.fromSupplier(() -> {
            return MessageDto.from(messageService.publish(request.author(), request.payload()));
        });
    }

    // DTOs
    public record MessageRequest(String author, String payload) {}
}
