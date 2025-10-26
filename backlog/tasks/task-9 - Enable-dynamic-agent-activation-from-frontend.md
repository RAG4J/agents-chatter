---
id: task-9
title: Enable dynamic agent activation from frontend
status: Done
assignee: []
created_date: '2025-10-23 22:50'
updated_date: '2025-10-26 11:45'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Allow agents to be started or stopped dynamically without relying on Spring profiles. Expose backend endpoints or commands so the frontend can request agent activation, and update the UI to allow users to toggle agents on/off at runtime.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Backend provides an API to list available agents and control their activation state without restarting the application.
- [ ] #2 Activating an agent registers it with the message bus and presence service; deactivating unsubscribes it cleanly.
- [ ] #3 Frontend surface lets users toggle agents on/off, reflecting current status in the presence list/header.
- [ ] #4 Documentation covers the new endpoints, activation flows, and any constraints (e.g., agent resource usage).
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Core Components
AgentRegistry (org.rag4j.chatter.web.agents.AgentRegistry)

Central service managing agent instances and their activation state
Thread-safe operations using ConcurrentHashMap
Tracks which agents are active/inactive
SubscriberAgent (updated)

Registers with AgentRegistry on initialization
Checks activation state before processing messages
When inactive, agents receive messages but exit early without processing
PresenceService (updated)

Tracks both online/offline status AND active/inactive state
Provides methods to change agent activation
Emits presence updates via SSE when activation state changes
PresenceController (updated)

New REST endpoints for activation control:
POST /api/presence/{agentName}/activate
POST /api/presence/{agentName}/deactivate
Data Model Updates
PresenceParticipant: Added boolean active field
PresenceStatus: Added boolean active field
PresenceDto: Added boolean active field
How It Works
Startup: All agents are instantiated (no more @Profile annotations)
Registration: Each agent registers with AgentRegistry with active=true
Message Processing: Agents check AgentRegistry.isActive() before processing
Activation Toggle: Frontend calls REST API → updates both AgentRegistry and PresenceService → emits SSE update
Real-time Updates: Frontend receives updated presence list via SSE stream
<!-- SECTION:PLAN:END -->
