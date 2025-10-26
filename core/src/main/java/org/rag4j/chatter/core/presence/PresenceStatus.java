package org.rag4j.chatter.core.presence;

public record PresenceStatus(PresenceParticipant participant, boolean online, int connectionCount, boolean active) {
}
