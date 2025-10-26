package org.rag4j.chatter.web.presence;

public record PresenceStatus(PresenceParticipant participant, boolean online, int connectionCount, boolean active) {
}
