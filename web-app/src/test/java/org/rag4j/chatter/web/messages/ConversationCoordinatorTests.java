package org.rag4j.chatter.web.messages;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rag4j.chatter.core.message.MessageEnvelope;
import org.rag4j.chatter.core.message.MessageEnvelope.MessageOrigin;
import org.rag4j.chatter.eventbus.bus.InMemoryMessageBus;
import org.rag4j.chatter.core.moderation.ModerationDecision;
import org.rag4j.chatter.core.moderation.ModerationEvent;
import org.rag4j.chatter.web.moderation.ModerationEventPublisher;
import org.rag4j.chatter.core.moderation.ModeratorService;

class ConversationCoordinatorTests {

    private MessageService messageService;
    private ConversationCoordinator coordinator;
    private ModeratorService moderatorService;
    private TestEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        messageService = new MessageService(new InMemoryMessageBus());
        moderatorService = context -> ModerationDecision.approve();
        eventPublisher = new TestEventPublisher();
        coordinator = new ConversationCoordinator(messageService, 2, moderatorService, eventPublisher);
    }

    @Test
    void humanMessageStartsNewThreadWithDepthZero() {
        ConversationCoordinator.PublishResult result = coordinator.handlePublish(
                ConversationCoordinator.PublishRequest.forHuman("alice", "hello", Optional.empty()));

        assertThat(result).isInstanceOf(ConversationCoordinator.PublishResult.Accepted.class);
        MessageEnvelope envelope = ((ConversationCoordinator.PublishResult.Accepted) result).envelope();

        assertThat(envelope.originType()).isEqualTo(MessageOrigin.HUMAN);
        assertThat(envelope.agentReplyDepth()).isZero();
        assertThat(envelope.threadId()).isNotNull();
    }

    @Test
    void parallelAgentRepliesHaveSameDepth() {
        // Human sends message (depth=0)
        ConversationCoordinator.PublishResult.Accepted human = (ConversationCoordinator.PublishResult.Accepted)
                coordinator.handlePublish(
                        ConversationCoordinator.PublishRequest.forHuman("alice", "What's sci-fi?", Optional.empty()));
        
        assertThat(human.envelope().agentReplyDepth()).isEqualTo(0);
        
        // Two agents reply to the same human message - both should have depth=1
        var agent1Reply = coordinator.handlePublish(
                ConversationCoordinator.PublishRequest.forAgent(
                        "StarWars Agent",
                        "Star Wars is the best!",
                        Optional.of(human.envelope().threadId()),
                        Optional.of(human.envelope().id()),
                        Optional.of(human.envelope())));
        
        var agent2Reply = coordinator.handlePublish(
                ConversationCoordinator.PublishRequest.forAgent(
                        "StarTrek Agent",
                        "Star Trek is superior!",
                        Optional.of(human.envelope().threadId()),
                        Optional.of(human.envelope().id()),
                        Optional.of(human.envelope())));
        
        assertThat(agent1Reply).isInstanceOf(ConversationCoordinator.PublishResult.Accepted.class);
        assertThat(agent2Reply).isInstanceOf(ConversationCoordinator.PublishResult.Accepted.class);
        
        MessageEnvelope agent1Envelope = ((ConversationCoordinator.PublishResult.Accepted) agent1Reply).envelope();
        MessageEnvelope agent2Envelope = ((ConversationCoordinator.PublishResult.Accepted) agent2Reply).envelope();
        
        // Both agents replying to the same depth-0 message should have depth=1
        assertThat(agent1Envelope.agentReplyDepth()).isEqualTo(1);
        assertThat(agent2Envelope.agentReplyDepth()).isEqualTo(1);
        
        // Now agents reply to each other's messages - both should have depth=2
        var agent1CounterReply = coordinator.handlePublish(
                ConversationCoordinator.PublishRequest.forAgent(
                        "StarWars Agent",
                        "Nope, Star Wars!",
                        Optional.of(human.envelope().threadId()),
                        Optional.of(agent2Envelope.id()),
                        Optional.of(agent2Envelope)));
        
        var agent2CounterReply = coordinator.handlePublish(
                ConversationCoordinator.PublishRequest.forAgent(
                        "StarTrek Agent",
                        "Wrong, Star Trek!",
                        Optional.of(human.envelope().threadId()),
                        Optional.of(agent1Envelope.id()),
                        Optional.of(agent1Envelope)));
        
        MessageEnvelope agent1Counter = ((ConversationCoordinator.PublishResult.Accepted) agent1CounterReply).envelope();
        MessageEnvelope agent2Counter = ((ConversationCoordinator.PublishResult.Accepted) agent2CounterReply).envelope();
        
        // Both replying to depth-1 messages should have depth=2
        assertThat(agent1Counter.agentReplyDepth()).isEqualTo(2);
        assertThat(agent2Counter.agentReplyDepth()).isEqualTo(2);
    }

    @Test
    void agentDepthLimitBlocksFurtherReplies() {
        ConversationCoordinator.PublishResult.Accepted human = (ConversationCoordinator.PublishResult.Accepted)
                coordinator.handlePublish(
                        ConversationCoordinator.PublishRequest.forHuman("alice", "hello", Optional.empty()));

        var firstAgent = coordinator.handlePublish(
                ConversationCoordinator.PublishRequest.forAgent(
                        "Echo Agent",
                        "echo hello",
                        Optional.of(human.envelope().threadId()),
                        Optional.of(human.envelope().id()),
                        Optional.of(human.envelope())));
        assertThat(firstAgent).isInstanceOf(ConversationCoordinator.PublishResult.Accepted.class);

        var secondAgent = coordinator.handlePublish(
                ConversationCoordinator.PublishRequest.forAgent(
                        "Echo Agent",
                        "echo again",
                        Optional.of(human.envelope().threadId()),
                        Optional.of(((ConversationCoordinator.PublishResult.Accepted) firstAgent).envelope().id()),
                        Optional.of(((ConversationCoordinator.PublishResult.Accepted) firstAgent).envelope())));
        assertThat(secondAgent).isInstanceOf(ConversationCoordinator.PublishResult.Accepted.class);

        var blocked = coordinator.handlePublish(
                ConversationCoordinator.PublishRequest.forAgent(
                        "Echo Agent",
                        "echo third",
                        Optional.of(human.envelope().threadId()),
                        Optional.of(((ConversationCoordinator.PublishResult.Accepted) secondAgent).envelope().id()),
                        Optional.of(((ConversationCoordinator.PublishResult.Accepted) secondAgent).envelope())));

        assertThat(blocked).isInstanceOf(ConversationCoordinator.PublishResult.Rejected.class);
        ConversationCoordinator.PublishResult.Rejected rejected = (ConversationCoordinator.PublishResult.Rejected) blocked;
        assertThat(rejected.attemptedDepth()).isEqualTo(3);
        assertThat(rejected.reason()).contains("Agent reply depth exceeded");
        assertThat(eventPublisher.events()).hasSize(1);
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
