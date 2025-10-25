package org.rag4j.chatter.application.port.in;

import java.util.List;

import org.rag4j.chatter.domain.agent.AgentDescriptor;

public interface AgentDiscoveryPort {

    List<AgentDescriptor> listAgents();
}
