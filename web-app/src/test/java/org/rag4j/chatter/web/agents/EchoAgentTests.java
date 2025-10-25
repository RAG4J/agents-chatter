package org.rag4j.chatter.web.agents;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rag4j.chatter.domain.message.MessageEnvelope;
import org.rag4j.chatter.domain.message.MessageEnvelope.MessageOrigin;
import org.rag4j.chatter.domain.moderation.AgentMessageContext;
import org.rag4j.chatter.domain.moderation.ModerationDecision;
import org.rag4j.chatter.domain.moderation.ModerationEvent;
import org.rag4j.chatter.eventbus.bus.MessageBus;
import org.rag4j.chatter.eventbus.bus.ReactorMessageBus;
import org.rag4j.chatter.web.messages.ConversationCoordinator;
import org.rag4j.chatter.web.messages.MessageService;
import org.rag4j.chatter.web.moderation.ModerationEventPublisher;
import org.rag4j.chatter.web.moderation.ModeratorService;
import org.rag4j.chatter.web.presence.PresenceService;

import reactor.test.StepVerifier;

class EchoAgentTests {

    private MessageBus messageBus;
    private MessageService messageService;
    private PresenceService presenceService;
    private ConversationCoordinator conversationCoordinator;
    private AgentPublisher agentPublisher;
    private EchoAgent subscriber;
    private TestModeratorService moderatorService;
    private TestEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        presenceService = mock(PresenceService.class);
        messageBus = new ReactorMessageBus();
        messageService = new MessageService(messageBus);
        moderatorService = new TestModeratorService();
        eventPublisher = new TestEventPublisher();
        conversationCoordinator = new ConversationCoordinator(messageService, 2, moderatorService, eventPublisher);
        agentPublisher = new AgentPublisher(conversationCoordinator);
        subscriber = new EchoAgent(messageService, agentPublisher, presenceService);
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
                .then(() -> conversationCoordinator.handlePublish(
                        ConversationCoordinator.PublishRequest.forHuman("User", "Hello from user", Optional.empty())))
                .assertNext(payload -> assertThat(payload).isEqualTo("echo Hello from user"))
                .expectComplete()
                .verify(Duration.ofSeconds(1));
    }

    @Test
    void skipsResponseWhenModeratorRejects() {
        moderatorService.setDelegate(context -> ModerationDecision.reject("blocked"));

        StepVerifier.create(
                        messageService.stream()
                                .filter(envelope -> EchoAgent.AGENT_NAME.equals(envelope.author()))
                                .take(1))
                .then(() -> conversationCoordinator.handlePublish(
                        ConversationCoordinator.PublishRequest.forHuman("User", "block this", Optional.empty())))
                .expectTimeout(Duration.ofMillis(200))
                .verify();
    }

    @Test
    void skipsHighDepthAgentMessages() {
        moderatorService.setDelegate(context -> ModerationDecision.approve());
        UUID threadId = UUID.randomUUID();
        MessageEnvelope incoming = MessageEnvelope.fromMetadata(
                "Loop Agent",
                "ping",
                threadId,
                Optional.of(UUID.randomUUID()),
                MessageOrigin.AGENT,
                2);

        StepVerifier.create(
                        messageService.stream()
                                .filter(envelope -> EchoAgent.AGENT_NAME.equals(envelope.author()))
                                .take(1))
                .then(() -> messageService.publish(incoming))
                .expectTimeout(Duration.ofMillis(200))
                .verify();
    }

    @Test
    void doesNotEchoOwnMessages() {
        StepVerifier.create(
                        messageService.stream()
                                .filter(envelope -> EchoAgent.AGENT_NAME.equals(envelope.author()))
                                .skip(1)
                                .take(1))
                .then(() -> messageService.publish(EchoAgent.AGENT_NAME, "echo recursion?"))
                .expectTimeout(Duration.ofMillis(200))
                .verify();
    }

    private static final class TestModeratorService implements ModeratorService {

        private Function<AgentMessageContext, ModerationDecision> delegate = context -> ModerationDecision.approve();

        void setDelegate(Function<AgentMessageContext, ModerationDecision> delegate) {
            this.delegate = delegate;
        }

        @Override
        public ModerationDecision evaluate(AgentMessageContext context) {
            return delegate.apply(context);
        }
    }

    private static final class TestEventPublisher extends ModerationEventPublisher {
        private final java.util.List<ModerationEvent> events = new java.util.ArrayList<>();

        @Override
        public void publish(ModerationEvent event) {
            events.add(event);
        }

        java.util.List<ModerationEvent> events() {
            return events;
        }
    }
}
