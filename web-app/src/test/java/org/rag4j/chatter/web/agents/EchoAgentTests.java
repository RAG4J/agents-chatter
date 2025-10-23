package org.rag4j.chatter.web.agents;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rag4j.chatter.eventbus.bus.MessageBus;
import org.rag4j.chatter.eventbus.bus.ReactorMessageBus;
import org.rag4j.chatter.eventbus.bus.MessageEnvelope;
import org.rag4j.chatter.web.messages.MessageService;
import org.rag4j.chatter.web.presence.PresenceService;
import reactor.test.StepVerifier;

class EchoAgentTests {

    private MessageBus messageBus;
    private MessageService messageService;
    private PresenceService presenceService;
    private EchoAgent subscriber;

    @BeforeEach
    void setUp() {
        presenceService = mock(PresenceService.class);
        messageBus = new ReactorMessageBus();
        messageService = new MessageService(messageBus);
        subscriber = new EchoAgent(messageService, presenceService);
        subscriber.subscribe();
    }

    @AfterEach
    void tearDown() {
        subscriber.shutdown();
    }

    @Test
    void echoesMessagesFromOtherAuthors() {
        StepVerifier.create(
                messageService.stream()
                    .filter(envelope -> EchoAgent.AGENT_NAME.equals(envelope.author()))
                    .map(MessageEnvelope::payload)
                    .take(1))
            .then(() -> messageService.publish("User", "Hello from user"))
            .assertNext(payload -> assertThat(payload).isEqualTo("echo Hello from user"))
            .expectComplete()
            .verify(Duration.ofSeconds(1));
    }

    @Test
    void doesNotEchoOwnMessages() {
        StepVerifier.create(
                        messageService.stream()
                                .filter(envelope -> EchoAgent.AGENT_NAME.equals(envelope.author()))
                                .skip(1) // skip the initial self-published message
                                .take(1))
                .then(() -> messageService.publish(EchoAgent.AGENT_NAME, "echo recursion?"))
                .expectTimeout(Duration.ofMillis(200))
                .verify();
    }
}
