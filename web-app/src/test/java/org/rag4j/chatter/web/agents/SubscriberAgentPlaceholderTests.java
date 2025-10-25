package org.rag4j.chatter.web.agents;

import static org.mockito.Mockito.mock;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Function;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rag4j.chatter.eventbus.bus.MessageBus;
import org.rag4j.chatter.eventbus.bus.ReactorMessageBus;
import org.rag4j.chatter.web.messages.ConversationCoordinator;
import org.rag4j.chatter.web.messages.MessageService;
import org.rag4j.chatter.web.moderation.ModerationDecision;
import org.rag4j.chatter.web.moderation.ModerationEvent;
import org.rag4j.chatter.web.moderation.ModerationEventPublisher;
import org.rag4j.chatter.web.moderation.ModeratorService;
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
        subscriber = new TestPlaceholderAgent(messageService, agentPublisher, presenceService);
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

        TestPlaceholderAgent(MessageService messageService, AgentPublisher agentPublisher, PresenceService presenceService) {
            super(AGENT_NAME, PresenceRole.AGENT, messageService, agentPublisher, presenceService);
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

    private static final class TestModeratorService implements ModeratorService {

        private Function<org.rag4j.chatter.web.moderation.AgentMessageContext, ModerationDecision> delegate =
                context -> ModerationDecision.approve();

        @Override
        public ModerationDecision evaluate(org.rag4j.chatter.web.moderation.AgentMessageContext context) {
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
