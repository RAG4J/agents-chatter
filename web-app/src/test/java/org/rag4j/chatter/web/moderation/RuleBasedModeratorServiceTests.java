package org.rag4j.chatter.web.moderation;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rag4j.chatter.core.moderation.AgentMessageContext;
import org.rag4j.chatter.core.moderation.ModerationDecision;
import org.rag4j.chatter.core.moderation.ModerationEvent;
import org.rag4j.chatter.web.moderation.ModerationEventPublisher;

class RuleBasedModeratorServiceTests {

    private ModeratorProperties properties;
    private MutableClock clock;
    private RuleBasedModeratorService moderatorService;
    private TestEventPublisher eventPublisher;
    private final UUID threadId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        properties = new ModeratorProperties();
        properties.setAgentCooldown(Duration.ofSeconds(5));
        properties.setDuplicateWindowSize(5);
        properties.setLoopAlternationsThreshold(2);
        properties.setAuthorHistorySize(6);
        clock = new MutableClock(Instant.parse("2025-10-24T10:00:00Z"));
        eventPublisher = new TestEventPublisher();
        moderatorService = new RuleBasedModeratorService(properties, clock, eventPublisher);
    }

    @Test
    void approvesByDefault() {
        ModerationDecision decision = moderatorService.evaluate(buildContext("Echo Agent", "echo hello"));
        assertThat(decision.status()).isEqualTo(ModerationDecision.Status.APPROVE);
    }

    @Test
    void rejectsDuplicatePayloadWithinThread() {
        ModerationDecision first = moderatorService.evaluate(buildContext("Echo Agent", "Repeat me"));
        assertThat(first.status()).isEqualTo(ModerationDecision.Status.APPROVE);

        clock.advance(Duration.ofSeconds(6));
        ModerationDecision duplicate = moderatorService.evaluate(buildContext("Echo Agent", "repeat me"));
        assertThat(duplicate.status()).isEqualTo(ModerationDecision.Status.REJECT);
        assertThat(duplicate.rationale()).contains("Duplicate");
    }

    @Test
    void enforcesPerAgentCooldown() {
        ModerationDecision first = moderatorService.evaluate(buildContext("Echo Agent", "cooldown test"));
        assertThat(first.status()).isEqualTo(ModerationDecision.Status.APPROVE);

        clock.advance(Duration.ofSeconds(1));
        ModerationDecision tooSoon = moderatorService.evaluate(buildContext("Echo Agent", "cooldown test 2"));
        assertThat(tooSoon.status()).isEqualTo(ModerationDecision.Status.REJECT);
        assertThat(tooSoon.rationale()).contains("Cooldown");

        clock.advance(Duration.ofSeconds(5));
        ModerationDecision afterCooldown = moderatorService.evaluate(buildContext("Echo Agent", "cooldown test 3"));
        assertThat(afterCooldown.status()).isEqualTo(ModerationDecision.Status.APPROVE);
    }

    @Test
    void detectsAlternatingAgentLoop() {
        // A -> B -> A should pass, second B triggers rejection
        assertThat(moderatorService.evaluate(buildContext("Agent A", "first")).status())
                .isEqualTo(ModerationDecision.Status.APPROVE);
        clock.advance(Duration.ofSeconds(6));
        assertThat(moderatorService.evaluate(buildContext("Agent B", "second")).status())
                .isEqualTo(ModerationDecision.Status.APPROVE);
        clock.advance(Duration.ofSeconds(6));
        assertThat(moderatorService.evaluate(buildContext("Agent A", "third")).status())
                .isEqualTo(ModerationDecision.Status.APPROVE);
        clock.advance(Duration.ofSeconds(6));

        ModerationDecision loop = moderatorService.evaluate(buildContext("Agent B", "fourth"));
        assertThat(loop.status()).isEqualTo(ModerationDecision.Status.REJECT);
        assertThat(loop.rationale()).contains("loop");
        assertThat(eventPublisher.events()).isNotEmpty();
    }

    @Test
    void publishesEventOnRejection() {
        // first approval
        moderatorService.evaluate(buildContext("Echo Agent", "hello"));
        clock.advance(Duration.ofSeconds(1));
        // second within cooldown triggers rejection and event
        ModerationDecision decision = moderatorService.evaluate(buildContext("Echo Agent", "hello again"));
        assertThat(decision.status()).isEqualTo(ModerationDecision.Status.REJECT);
        assertThat(eventPublisher.events())
                .hasSize(1)
                .first()
                .satisfies(event -> {
                    assertThat(event.threadId()).isEqualTo(threadId);
                    assertThat(event.agent()).isEqualTo("Echo Agent");
                    assertThat(event.rationale()).contains("Cooldown");
                });
    }

    private AgentMessageContext buildContext(String agentName, String payload) {
        return new AgentMessageContext(
                agentName,
                payload,
                threadId,
                Optional.empty(),
                1,
                Optional.empty());
    }

    private static final class MutableClock extends Clock {
        private Instant instant;

        private MutableClock(Instant instant) {
            this.instant = instant;
        }

        void advance(Duration duration) {
            instant = instant.plus(duration);
        }

        @Override
        public ZoneId getZone() {
            return ZoneId.of("UTC");
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return new MutableClock(instant);
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }

    private static final class TestEventPublisher extends ModerationEventPublisher {
        private final java.util.List<ModerationEvent> events = new java.util.ArrayList<>();

        @Override
        public void publish(ModerationEvent event) {
            events.add(event);
        }

        public java.util.List<ModerationEvent> events() {
            return events;
        }
    }
}
