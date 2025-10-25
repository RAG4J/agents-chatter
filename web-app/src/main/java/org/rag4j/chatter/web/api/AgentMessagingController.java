package org.rag4j.chatter.web.api;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.rag4j.chatter.application.port.in.conversation.AgentMessagingCallback;
import org.rag4j.chatter.application.port.in.conversation.PublishCommand;
import org.rag4j.chatter.application.port.in.conversation.PublishResult;
import org.rag4j.chatter.domain.message.MessageEnvelope;
import org.rag4j.chatter.web.messages.MessageDto;
import org.rag4j.chatter.web.messages.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(path = "/api/agents/{agentName}/messages", produces = MediaType.APPLICATION_JSON_VALUE)
public class AgentMessagingController {

    private static final Logger logger = LoggerFactory.getLogger(AgentMessagingController.class);

    private final AgentMessagingCallback agentMessaging;
    private final MessageService messageService;

    public AgentMessagingController(AgentMessagingCallback agentMessaging, MessageService messageService) {
        this.agentMessaging = agentMessaging;
        this.messageService = messageService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public MessageDto publish(@PathVariable("agentName") String agentName, @RequestBody AgentMessageRequest request) {
        Optional<UUID> threadId = request.threadId().flatMap(this::parseUuid);
        Optional<UUID> parentMessageId = request.parentMessageId().flatMap(this::parseUuid);
        Optional<MessageEnvelope> parentEnvelope = parentMessageId.flatMap(this::findEnvelope);

        PublishCommand command = PublishCommand.forAgent(
                agentName,
                request.payload(),
                threadId,
                parentMessageId,
                parentEnvelope);

        PublishResult result;
        try {
            result = agentMessaging.publish(command);
        } catch (RuntimeException ex) {
            logger.error("Agent {} failed to publish message", agentName, ex);
            throw ex;
        }
        if (result instanceof PublishResult.Accepted accepted) {
            return MessageDto.from(accepted.envelope());
        }

        PublishResult.Rejected rejected = (PublishResult.Rejected) result;
        throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, rejected.reason());
    }

    private Optional<UUID> parseUuid(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(UUID.fromString(value));
        }
        catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid UUID value: " + value, ex);
        }
    }

    private Optional<MessageEnvelope> findEnvelope(UUID id) {
        List<MessageEnvelope> history = messageService.getHistory();
        return history.stream().filter(envelope -> id.equals(envelope.id())).findFirst();
    }

    public record AgentMessageRequest(
            String payload,
            Optional<String> threadId,
            Optional<String> parentMessageId) {

        public AgentMessageRequest {
            if (payload == null || payload.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "payload must not be blank");
            }
            threadId = threadId == null ? Optional.empty() : threadId;
            parentMessageId = parentMessageId == null ? Optional.empty() : parentMessageId;
        }
    }
}
