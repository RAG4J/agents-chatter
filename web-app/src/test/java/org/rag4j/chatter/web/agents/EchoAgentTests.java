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
import org.rag4j.chatter.application.messages.ConversationApplicationService;
import org.rag4j.chatter.application.port.in.AgentMessageSubscriptionPort;
import org.rag4j.chatter.application.port.out.ModerationEventPort;
import org.rag4j.chatter.application.port.out.ModerationPolicyPort;
import org.rag4j.chatter.domain.message.MessageEnvelope;
import org.rag4j.chatter.domain.message.MessageEnvelope.MessageOrigin;
import org.rag4j.chatter.domain.moderation.AgentMessageContext;
import org.rag4j.chatter.domain.moderation.ModerationDecision;
import org.rag4j.chatter.domain.moderation.ModerationEvent;
import org.rag4j.chatter.eventbus.bus.MessageBus;
import org.rag4j.chatter.eventbus.bus.ReactorMessageBus;
import org.rag4j.chatter.web.messages.ConversationCoordinator;
import org.rag4j.chatter.web.messages.MessageService;
import org.rag4j.chatter.web.presence.PresenceService;

import reactor.test.StepVerifier;

class EchoAgentTests {

    private MessageBus messageBus;
    private MessageService messageService;
    private PresenceService presenceService;
    private ConversationCoordinator conversationCoordinator;
    private AgentPublisher agentPublisher;
    private EchoAgent subscriber;
    private AgentMessageSubscriptionPort subscriptionPort;
    private TestAgentRegistry agentRegistry;
    private TestModeratorService moderatorService;
    private TestEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        presenceService = mock(PresenceService.class);
        messageBus = new ReactorMessageBus();
        messageService = new MessageService(messageBus);
        subscriptionPort = new MessageSubscriptionAdapter(messageService);
        moderatorService = new TestModeratorService();
        eventPublisher = new TestEventPublisher();
        agentRegistry = new TestAgentRegistry();
        ConversationApplicationService service = new ConversationApplicationService(
                messageService,
                moderatorService,
                eventPublisher,
                2);
        conversationCoordinator = new ConversationCoordinator(service);
        agentPublisher = new AgentPublisher(service);
        subscriber = new EchoAgent(subscriptionPort, agentPublisher, agentRegistry, presenceService);
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

    private static final class TestModeratorService implements ModerationPolicyPort {

        private Function<AgentMessageContext, ModerationDecision> delegate = context -> ModerationDecision.approve();

        void setDelegate(Function<AgentMessageContext, ModerationDecision> delegate) {
            this.delegate = delegate;
        }

        @Override
        public ModerationDecision evaluate(AgentMessageContext context) {
            return delegate.apply(context);
        }
    }

    private static final class TestEventPublisher implements ModerationEventPort {
        private final java.util.List<ModerationEvent> events = new java.util.ArrayList<>();

        @Override
        public void publish(ModerationEvent event) {
            events.add(event);
        }

        java.util.List<ModerationEvent> events() {
            return events;
        }
    }

    private static final class TestAgentRegistry implements org.rag4j.chatter.application.port.in.AgentRegistrationUseCase {

        private final java.util.Set<String> registeredAgents = new java.util.HashSet<>();

        @Override
        public void register(org.rag4j.chatter.domain.agent.AgentDescriptor descriptor) {
            registeredAgents.add(descriptor.name());
        }

        @Override
        public void unregister(String agentName) {
            registeredAgents.remove(agentName);
        }

        boolean isRegistered(String agentName) {
            return registeredAgents.contains(agentName);
        }
    }
}
