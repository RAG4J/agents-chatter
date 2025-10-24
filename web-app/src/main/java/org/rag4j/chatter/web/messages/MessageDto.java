package org.rag4j.chatter.web.messages;

import java.util.UUID;

import org.rag4j.chatter.eventbus.bus.MessageEnvelope;

public record MessageDto(
        String id,
        String author,
        String payload,
        String timestamp,
        String threadId,
        String parentMessageId,
        String originType,
        int agentReplyDepth) {

    public static MessageDto from(MessageEnvelope envelope) {
        return new MessageDto(
            envelope.id().toString(),
            envelope.author(),
            envelope.payload(),
            envelope.createdAt().toString(),
            envelope.threadId().toString(),
            envelope.parentMessageId().map(UUID::toString).orElse(null),
            envelope.originType().name(),
            envelope.agentReplyDepth()
        );
    }
}
