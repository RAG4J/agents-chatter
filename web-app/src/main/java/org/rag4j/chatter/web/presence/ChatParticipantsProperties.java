package org.rag4j.chatter.web.presence;

import org.rag4j.chatter.core.presence.PresenceRole;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "chat.participants")
public class ChatParticipantsProperties {

    private ParticipantConfig human = new ParticipantConfig("You", PresenceRole.HUMAN);

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
