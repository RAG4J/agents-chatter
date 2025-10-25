package org.rag4j.chatter.web.messages;

import java.util.Optional;
import java.util.UUID;

import org.rag4j.chatter.application.port.in.conversation.ConversationUseCase;
import org.rag4j.chatter.application.port.in.conversation.PublishCommand;
import org.rag4j.chatter.application.port.in.conversation.PublishResult;
import org.rag4j.chatter.domain.message.MessageEnvelope;
import org.rag4j.chatter.domain.message.MessageEnvelope.MessageOrigin;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Spring adapter delegating message publication orchestration to the application layer.
 */
@Component
public class ConversationCoordinator {

    private final ConversationUseCase conversationUseCase;

    public ConversationCoordinator(@Qualifier("conversationUseCase") ConversationUseCase conversationUseCase) {
        this.conversationUseCase = conversationUseCase;
    }

    public PublishResult handlePublish(PublishRequest request) {
        PublishCommand command = new PublishCommand(
                request.author(),
                request.payload(),
                request.originType(),
                request.threadId(),
                request.parentMessageId(),
                request.parentEnvelope());
        return conversationUseCase.publish(command);
    }

    public record PublishRequest(
            String author,
            String payload,
            MessageOrigin originType,
            Optional<UUID> threadId,
            Optional<UUID> parentMessageId,
            Optional<MessageEnvelope> parentEnvelope) {

        public PublishRequest {
            if (author == null || author.isBlank()) {
                throw new IllegalArgumentException("author must not be blank");
            }
            if (payload == null) {
                throw new IllegalArgumentException("payload must not be null");
            }
            originType = originType == null ? MessageOrigin.UNKNOWN : originType;
            threadId = threadId == null ? Optional.empty() : threadId;
            parentMessageId = parentMessageId == null ? Optional.empty() : parentMessageId;
            parentEnvelope = parentEnvelope == null ? Optional.empty() : parentEnvelope;
        }

        public static PublishRequest forHuman(String author, String payload, Optional<UUID> threadId) {
            return new PublishRequest(author, payload, MessageOrigin.HUMAN, threadId, Optional.empty(), Optional.empty());
        }

        public static PublishRequest forAgent(String author,
                String payload,
                Optional<UUID> threadId,
                Optional<UUID> parentMessageId,
                Optional<MessageEnvelope> parentEnvelope) {
            return new PublishRequest(author, payload, MessageOrigin.AGENT, threadId, parentMessageId, parentEnvelope);
        }
    }
}
