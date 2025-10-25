package org.rag4j.chatter.domain.message;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class MessageEnvelopeTests {

    @Test
    void fromCreatesNewEnvelopeWithDefaults() {
        MessageEnvelope envelope = MessageEnvelope.from("alice", "hello");

        assertEquals("alice", envelope.author());
        assertEquals("hello", envelope.payload());
        assertEquals(envelope.id(), envelope.threadId());
        assertTrue(envelope.parentMessageId().isEmpty());
        assertEquals(MessageEnvelope.MessageOrigin.UNKNOWN, envelope.originType());
        assertEquals(0, envelope.agentReplyDepth());
    }

    @Test
    void fromMetadataValidatesDepth() {
        UUID threadId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> MessageEnvelope.fromMetadata(
                "agent",
                "payload",
                threadId,
                Optional.empty(),
                MessageEnvelope.MessageOrigin.AGENT,
                -1));
    }
}
