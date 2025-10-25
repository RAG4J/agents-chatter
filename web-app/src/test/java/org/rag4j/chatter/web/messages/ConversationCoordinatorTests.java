package org.rag4j.chatter.web.messages;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rag4j.chatter.application.messages.ConversationApplicationService;
import org.rag4j.chatter.application.messages.PublishResult;
import org.rag4j.chatter.application.port.out.MessagePublicationPort;
import org.rag4j.chatter.application.port.out.ModerationEventPort;
import org.rag4j.chatter.application.port.out.ModerationPolicyPort;
import org.rag4j.chatter.domain.message.MessageEnvelope;
import org.rag4j.chatter.domain.message.MessageEnvelope.MessageOrigin;
import org.rag4j.chatter.domain.moderation.AgentMessageContext;
import org.rag4j.chatter.domain.moderation.ModerationDecision;
import org.rag4j.chatter.domain.moderation.ModerationEvent;
import org.rag4j.chatter.eventbus.bus.ReactorMessageBus;

class ConversationCoordinatorTests {

    private MessageService messageService;
    private ConversationCoordinator coordinator;
    private TestModeratorService moderatorService;
    private TestEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        messageService = new MessageService(new ReactorMessageBus());
        moderatorService = new TestModeratorService();
        eventPublisher = new TestEventPublisher();
        ConversationApplicationService service = new ConversationApplicationService(
                messageService,
                moderatorService,
                eventPublisher,
                2);
        coordinator = new ConversationCoordinator(service);
    }

    @Test
    void humanMessageStartsNewThreadWithDepthZero() {
        PublishResult result = coordinator.handlePublish(
                ConversationCoordinator.PublishRequest.forHuman("alice", "hello", Optional.empty()));

        assertThat(result).isInstanceOf(PublishResult.Accepted.class);
        MessageEnvelope envelope = ((PublishResult.Accepted) result).envelope();

        assertThat(envelope.originType()).isEqualTo(MessageOrigin.HUMAN);
        assertThat(envelope.agentReplyDepth()).isZero();
        assertThat(envelope.threadId()).isNotNull();
    }

    @Test
    void agentDepthLimitBlocksFurtherReplies() {
        PublishResult.Accepted human = (PublishResult.Accepted) coordinator.handlePublish(
                ConversationCoordinator.PublishRequest.forHuman("alice", "hello", Optional.empty()));

        PublishResult firstAgent = coordinator.handlePublish(
                ConversationCoordinator.PublishRequest.forAgent(
                        "Echo Agent",
                        "echo hello",
                        Optional.of(human.envelope().threadId()),
                        Optional.of(human.envelope().id()),
                        Optional.of(human.envelope())));
        assertThat(firstAgent).isInstanceOf(PublishResult.Accepted.class);

        PublishResult secondAgent = coordinator.handlePublish(
                ConversationCoordinator.PublishRequest.forAgent(
                        "Echo Agent",
                        "echo again",
                        Optional.of(human.envelope().threadId()),
                        Optional.of(((PublishResult.Accepted) firstAgent).envelope().id()),
                        Optional.of(((PublishResult.Accepted) firstAgent).envelope())));
        assertThat(secondAgent).isInstanceOf(PublishResult.Accepted.class);

        PublishResult blocked = coordinator.handlePublish(
                ConversationCoordinator.PublishRequest.forAgent(
                        "Echo Agent",
                        "echo third",
                        Optional.of(human.envelope().threadId()),
                        Optional.of(((PublishResult.Accepted) secondAgent).envelope().id()),
                        Optional.of(((PublishResult.Accepted) secondAgent).envelope())));

        assertThat(blocked).isInstanceOf(PublishResult.Rejected.class);
        PublishResult.Rejected rejected = (PublishResult.Rejected) blocked;
        assertThat(rejected.attemptedDepth()).isEqualTo(3);
        assertThat(rejected.reason()).contains("Agent reply depth exceeded");
        assertThat(eventPublisher.events()).hasSize(1);
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

    private static final class TestModeratorService implements ModerationPolicyPort {

        private ModerationDecision decision = ModerationDecision.approve();

        void setDecision(ModerationDecision decision) {
            this.decision = decision;
        }

        @Override
        public ModerationDecision evaluate(AgentMessageContext context) {
            return decision;
        }
    }
}
