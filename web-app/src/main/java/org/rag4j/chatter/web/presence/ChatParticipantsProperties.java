package org.rag4j.chatter.web.presence;

import java.util.ArrayList;
import java.util.List;

import org.rag4j.chatter.web.agents.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "chat.participants")
public class ChatParticipantsProperties {

    private List<ParticipantConfig> agents = new ArrayList<>(List.of(
            new ParticipantConfig(EchoAgent.AGENT_NAME, PresenceRole.AGENT),
            new ParticipantConfig(FootballAgent.AGENT_NAME, PresenceRole.AGENT),
            new ParticipantConfig(ApeldoornITScheduleAgent.AGENT_NAME, PresenceRole.AGENT),
            new ParticipantConfig(StarWarsAgent.AGENT_NAME, PresenceRole.AGENT),
            new ParticipantConfig(StarTrekAgent.AGENT_NAME, PresenceRole.AGENT)
    ));

    private ParticipantConfig human = new ParticipantConfig("You", PresenceRole.HUMAN);

    public List<ParticipantConfig> getAgents() {
        return agents;
    }

    public void setAgents(List<ParticipantConfig> agents) {
        this.agents = agents;
    }

    public ParticipantConfig getHuman() {
        return human;
    }

    public void setHuman(ParticipantConfig human) {
        this.human = human;
    }

    public record ParticipantConfig(String name, PresenceRole role) {
        public ParticipantConfig {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Participant name must be provided");
            }
        }
    }
}
