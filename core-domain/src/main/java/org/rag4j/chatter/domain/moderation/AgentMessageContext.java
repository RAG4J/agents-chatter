package org.rag4j.chatter.domain.moderation;

import java.util.Optional;
import java.util.UUID;

import org.rag4j.chatter.domain.message.MessageEnvelope;

public record AgentMessageContext(
        String agentName,
        String payload,
        UUID threadId,
        Optional<UUID> parentMessageId,
        int agentReplyDepth,
        Optional<MessageEnvelope> parentMessage) {

    public AgentMessageContext {
        if (agentName == null || agentName.isBlank()) {
            throw new IllegalArgumentException("agentName must not be blank");
        }
        if (payload == null) {
            throw new IllegalArgumentException("payload must not be null");
        }
        if (threadId == null) {
            throw new IllegalArgumentException("threadId must not be null");
        }
        if (agentReplyDepth < 0) {
            throw new IllegalArgumentException("agentReplyDepth must be >= 0");
        }
    }
}
