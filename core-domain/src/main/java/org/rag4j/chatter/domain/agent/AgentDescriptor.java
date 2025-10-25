package org.rag4j.chatter.domain.agent;

import java.util.Objects;

/**
 * Describes an agent participating in the system (embedded or external).
 */
public record AgentDescriptor(
        String name,
        String displayName,
        AgentType type,
        String endpoint) {

    public AgentDescriptor {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        displayName = displayName == null || displayName.isBlank() ? name : displayName;
        type = Objects.requireNonNull(type, "type must not be null");
        endpoint = endpoint == null ? "" : endpoint;
    }

    public enum AgentType {
        EMBEDDED,
        REMOTE
    }
}
