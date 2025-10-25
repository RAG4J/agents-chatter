package org.rag4j.chatter.web.agents;

import static org.mockito.Mockito.mock;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Function;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rag4j.chatter.application.messages.ConversationApplicationService;
import org.rag4j.chatter.application.port.in.AgentMessageSubscriptionPort;
import org.rag4j.chatter.application.port.out.ModerationEventPort;
import org.rag4j.chatter.application.port.out.ModerationPolicyPort;
import org.rag4j.chatter.domain.moderation.AgentMessageContext;
import org.rag4j.chatter.domain.moderation.ModerationDecision;
import org.rag4j.chatter.domain.moderation.ModerationEvent;
import org.rag4j.chatter.eventbus.bus.MessageBus;
import org.rag4j.chatter.eventbus.bus.ReactorMessageBus;
import org.rag4j.chatter.web.messages.ConversationCoordinator;
import org.rag4j.chatter.web.messages.MessageService;
import org.rag4j.chatter.web.presence.PresenceRole;
import org.rag4j.chatter.web.presence.PresenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class SubscriberAgentPlaceholderTests {

    private MessageBus messageBus;
    private MessageService messageService;
    private PresenceService presenceService;
    private ConversationCoordinator conversationCoordinator;
    private AgentPublisher agentPublisher;
    private TestPlaceholderAgent subscriber;
    private AgentMessageSubscriptionPort subscriptionPort;
    private TestModeratorService moderatorService;
    private TestEventPublisher eventPublisher;
    private TestAgentRegistry agentRegistry;

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
        subscriber = new TestPlaceholderAgent(subscriptionPort, agentPublisher, agentRegistry, presenceService);
        subscriber.subscribe();
    }

    @AfterEach
    void tearDown() {
        subscriber.shutdown();
    }

    @Test
    void suppressesPlaceholderResponses() {
        StepVerifier.create(
                        messageService.stream()
                                .filter(envelope -> TestPlaceholderAgent.AGENT_NAME.equals(envelope.author()))
                                .take(1))
                .then(() -> conversationCoordinator.handlePublish(
                        ConversationCoordinator.PublishRequest.forHuman("User", "Hello from user", Optional.empty())))
                .expectTimeout(Duration.ofMillis(200))
                .verify();
    }

    private static final class TestPlaceholderAgent extends SubscriberAgent {
        private static final Logger logger = LoggerFactory.getLogger(TestPlaceholderAgent.class);
        private static final String AGENT_NAME = "Placeholder Agent";

        TestPlaceholderAgent(AgentMessageSubscriptionPort subscriptionPort,
                AgentPublisher agentPublisher,
                TestAgentRegistry agentRegistry,
                PresenceService presenceService) {
            super(AGENT_NAME, PresenceRole.AGENT, subscriptionPort, agentPublisher, agentRegistry, presenceService);
        }

        @Override
        Logger logger() {
            return logger;
        }

        @Override
        Mono<String> messagePayload(String incomingPayload) {
            return Mono.just(SubscriberAgent.NO_MESSAGE_PLACEHOLDER);
        }
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

        @Override
        public void register(org.rag4j.chatter.domain.agent.AgentDescriptor descriptor) {
            // no-op
        }

        @Override
        public void unregister(String agentName) {
            // no-op
        }
    }
}
