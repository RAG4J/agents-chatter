package org.rag4j.chatter.web.socket;

import java.net.URI;
import java.util.Locale;

import org.rag4j.chatter.web.messages.MessageDto;
import org.rag4j.chatter.web.messages.MessageService;
import org.rag4j.chatter.web.presence.PresenceService;
import org.rag4j.chatter.web.presence.PresenceRole;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * WebSocket handler bridging clients to the message bus.
 */
@Component
public class MessageWebSocketHandler implements WebSocketHandler {

    private final ObjectMapper objectMapper;
    private final MessageService messageService;
    private final PresenceService presenceService;

    public MessageWebSocketHandler(ObjectMapper objectMapper, MessageService messageService, PresenceService presenceService) {
        this.objectMapper = objectMapper;
        this.messageService = messageService;
        this.presenceService = presenceService;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String participantName = resolveParticipant(session.getHandshakeInfo().getUri());
        presenceService.markOnline(participantName, inferRole(participantName));

        Flux<WebSocketMessage> outbound = messageService.stream()
            .map(MessageDto::from)
            .map(this::encode)
            .map(session::textMessage);

        Mono<Void> receive = session.receive()
            .map(WebSocketMessage::getPayloadAsText)
            .flatMap(this::decodeRequest)
            .flatMap(request -> Mono.fromSupplier(() -> MessageDto.from(
                    messageService.publish(request.author(), request.payload()))))
            .then();

        Mono<Void> send = session.send(outbound);

        return Mono.when(send, receive)
            .doFinally(signalType -> presenceService.markOffline(participantName));
    }

    private String resolveParticipant(URI uri) {
        if (uri == null) {
            return "You";
        }
        var components = UriComponentsBuilder.fromUri(uri).build();
        String participant = components.getQueryParams().getFirst("participant");
        if (participant == null || participant.isBlank()) {
            return "You";
        }
        return participant.trim();
    }

    private PresenceRole inferRole(String name) {
        String lower = name.toLowerCase(Locale.ENGLISH);
        if (lower.contains("agent")) {
            return PresenceRole.AGENT;
        }
        return PresenceRole.HUMAN;
    }

    private String encode(MessageDto dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to encode message", ex);
        }
    }

    private Mono<MessageRequest> decodeRequest(String payload) {
        try {
            return Mono.just(objectMapper.readValue(payload, MessageRequest.class));
        } catch (JsonProcessingException ex) {
            return Mono.empty();
        }
    }

    private record MessageRequest(String author, String payload) {
    }
}
