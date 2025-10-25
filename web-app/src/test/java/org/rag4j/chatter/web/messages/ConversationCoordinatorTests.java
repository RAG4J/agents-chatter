package org.rag4j.chatter.web.messages;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rag4j.chatter.eventbus.bus.MessageEnvelope;
import org.rag4j.chatter.eventbus.bus.MessageEnvelope.MessageOrigin;
import org.rag4j.chatter.eventbus.bus.ReactorMessageBus;
import org.rag4j.chatter.web.moderation.ModerationDecision;
import org.rag4j.chatter.web.moderation.ModeratorService;

class ConversationCoordinatorTests {

    private MessageService messageService;
    private ConversationCoordinator coordinator;
    private ModeratorService moderatorService;

    @BeforeEach
    void setUp() {
        messageService = new MessageService(new ReactorMessageBus());
        moderatorService = context -> ModerationDecision.approve();
        coordinator = new ConversationCoordinator(messageService, 2, moderatorService);
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
    }
}
