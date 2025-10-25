package org.rag4j.chatter.domain.agent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AgentDescriptorTests {

    @Test
    void defaultsDisplayNameToNameWhenBlank() {
        AgentDescriptor descriptor = new AgentDescriptor("agent-1", "", AgentDescriptor.AgentType.REMOTE, "http://localhost");
        assertEquals("agent-1", descriptor.displayName());
    }

    @Test
    void requiresName() {
        assertThrows(IllegalArgumentException.class,
                () -> new AgentDescriptor("", "Display", AgentDescriptor.AgentType.EMBEDDED, null));
    }

    @Test
    void retainsEndpoint() {
        AgentDescriptor descriptor = new AgentDescriptor("agent", "Agent", AgentDescriptor.AgentType.REMOTE, "http://remote");
        assertEquals("http://remote", descriptor.endpoint());
    }
}
