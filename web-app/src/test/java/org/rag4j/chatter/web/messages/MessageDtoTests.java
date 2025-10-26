package org.rag4j.chatter.web.messages;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.rag4j.chatter.core.message.MessageEnvelope;
import org.rag4j.chatter.core.message.MessageEnvelope.MessageOrigin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

class MessageDtoTests {

    private final ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();

    @Test
    void fromCopiesMetadataFields() {
        UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID threadId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID parentId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        Instant createdAt = Instant.parse("2025-10-24T10:00:00Z");

        MessageEnvelope envelope = new MessageEnvelope(
                id,
                "TestAgent",
                "payload",
                createdAt,
                threadId,
                Optional.of(parentId),
                MessageOrigin.AGENT,
                2);

        MessageDto dto = MessageDto.from(envelope);

        assertThat(dto.id()).isEqualTo(id.toString());
        assertThat(dto.threadId()).isEqualTo(threadId.toString());
        assertThat(dto.parentMessageId()).isEqualTo(parentId.toString());
        assertThat(dto.originType()).isEqualTo("AGENT");
        assertThat(dto.agentReplyDepth()).isEqualTo(2);
    }

    @Test
    void serialisesNullParentWhenMissing() throws Exception {
        MessageEnvelope envelope = MessageEnvelope.from("You", "Hello world");

        MessageDto dto = MessageDto.from(envelope);

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"parentMessageId\":null");
        assertThat(json).contains("\"threadId\":\"" + envelope.threadId() + "\"");
        assertThat(json).contains("\"originType\":\"UNKNOWN\"");
    }
}
