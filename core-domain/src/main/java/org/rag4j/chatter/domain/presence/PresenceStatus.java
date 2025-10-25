package org.rag4j.chatter.domain.presence;

public record PresenceStatus(PresenceParticipant participant, boolean online, int connectionCount) {
}
