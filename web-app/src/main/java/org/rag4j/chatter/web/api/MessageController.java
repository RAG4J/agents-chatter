package org.rag4j.chatter.web.api;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.rag4j.chatter.eventbus.bus.MessageEnvelope.MessageOrigin;
import org.rag4j.chatter.web.messages.ConversationCoordinator;
import org.rag4j.chatter.web.messages.MessageDto;
import org.rag4j.chatter.web.messages.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;

/** REST endpoints for chat message retrieval and publishing. */
@RestController
@RequestMapping("/api/messages")
@CrossOrigin
@Validated
public class MessageController {

    private final MessageService messageService;
    private final ConversationCoordinator conversationCoordinator;

    public MessageController(MessageService messageService, ConversationCoordinator conversationCoordinator) {
        this.messageService = messageService;
        this.conversationCoordinator = conversationCoordinator;
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
        return Mono.fromSupplier(() -> publishThroughCoordinator(request));
    }

    @DeleteMapping
    public Mono<Void> clearMessages() {
        return Mono.fromRunnable(messageService::clearHistory);
    }

    private MessageDto publishThroughCoordinator(MessageRequest request) {
        MessageOrigin origin = request.originType()
                .map(String::toUpperCase)
                .map(MessageOrigin::valueOf)
                .orElse(MessageOrigin.HUMAN);

        Optional<UUID> threadId = request.threadId().flatMap(this::safeParseUuid);
        Optional<UUID> parentId = request.parentMessageId().flatMap(this::safeParseUuid);

        ConversationCoordinator.PublishRequest publishRequest = new ConversationCoordinator.PublishRequest(
                request.author(),
                request.payload(),
                origin,
                threadId,
                parentId,
                Optional.empty());

        ConversationCoordinator.PublishResult result = conversationCoordinator.handlePublish(publishRequest);
        if (result instanceof ConversationCoordinator.PublishResult.Accepted accepted) {
            return MessageDto.from(accepted.envelope());
        }

        ConversationCoordinator.PublishResult.Rejected rejected = (ConversationCoordinator.PublishResult.Rejected) result;
        throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, rejected.reason());
    }

    private Optional<UUID> safeParseUuid(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(UUID.fromString(value));
        }
        catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid UUID value: " + value);
        }
    }

    // DTOs
    public record MessageRequest(String author,
                                 String payload,
                                 Optional<String> originType,
                                 Optional<String> threadId,
                                 Optional<String> parentMessageId) {

        public MessageRequest {
            originType = originType == null ? Optional.empty() : originType;
            threadId = threadId == null ? Optional.empty() : threadId;
            parentMessageId = parentMessageId == null ? Optional.empty() : parentMessageId;
        }
    }
}
