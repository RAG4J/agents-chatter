package org.rag4j.chatter.web.messages;

import org.rag4j.chatter.eventbus.bus.MessageEnvelope;

public record MessageDto(String id, String author, String payload, String timestamp) {

    public static MessageDto from(MessageEnvelope envelope) {
        return new MessageDto(
            envelope.id().toString(),
            envelope.author(),
            envelope.payload(),
            envelope.createdAt().toString()
        );
    }
}
