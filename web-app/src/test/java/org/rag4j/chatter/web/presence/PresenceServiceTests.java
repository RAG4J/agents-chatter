package org.rag4j.chatter.web.presence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rag4j.chatter.core.presence.PresenceRole;

import org.rag4j.chatter.core.presence.PresenceStatus;
import org.rag4j.chatter.web.agents.AgentRegistry;
import reactor.test.StepVerifier;

class PresenceServiceTests {

    private PresenceService presenceService;

    @BeforeEach
    void setUp() {
        ChatParticipantsProperties properties = new ChatParticipantsProperties();
        AgentRegistry agentRegistry = mock(AgentRegistry.class);
        presenceService = new PresenceService(properties, agentRegistry);
    }

    @Test
    void registersAndUnregistersSessions() {
        presenceService.markOnline("You", PresenceRole.HUMAN);
        presenceService.markOnline("Echo Agent", PresenceRole.AGENT);

        List<PresenceStatus> snapshot = presenceService.snapshot();
        assertThat(snapshot)
            .filteredOn(PresenceStatus::online)
            .extracting(status -> status.participant().name())
            .containsExactlyInAnyOrder("Echo Agent", "You");

        presenceService.markOffline("You");
        snapshot = presenceService.snapshot();
        assertThat(snapshot)
            .filteredOn(PresenceStatus::online)
            .extracting(status -> status.participant().name())
            .containsExactly("Echo Agent");
    }

    @Test
    void emitsPresenceUpdatesOnStream() {
        StepVerifier.create(presenceService.stream())
            .expectNextMatches(list -> list.stream().noneMatch(PresenceStatus::online))
            .then(() -> presenceService.markOnline("You", PresenceRole.HUMAN))
            .expectNextMatches(list -> list.stream().anyMatch(status -> status.participant().name().equals("You") && status.online()))
            .then(() -> presenceService.markOffline("You"))
            .expectNextMatches(list -> list.stream().noneMatch(PresenceStatus::online))
            .thenCancel()
            .verify();
    }
}
