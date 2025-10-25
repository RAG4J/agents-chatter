---
id: task-29
title: Introduce PresencePort and isolate presence tracking infrastructure
status: To Do
assignee: []
created_date: '2025-10-25 21:27'
updated_date: '2025-10-25 21:57'
labels:
  - architecture
  - hexagonal
  - presence
dependencies:
  - task-23
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Add an application-layer PresencePort (and related use cases if needed) that encapsulates participant availability updates. Refactor PresenceService and SSE/WebSocket adapters to implement the port, eliminating direct Reactor sink usage from domain-facing code. Provide clear contracts for persistence so alternate implementations (e.g., Redis, database) can be added later.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Presence-related collaborators depend on a PresencePort contract located in the application-services module.
- [ ] #2 Current PresenceService becomes an adapter implementing the port, with Reactor-specific code confined to the adapter layer.
- [ ] #3 Controllers/agents update to inject the port interface rather than the concrete service.
- [ ] #4 Add tests covering presence updates via the new port abstraction.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Review current presence tracking code (PresenceService, WebSocket handlers, SSE endpoints) to document responsibilities and dependencies.

Define PresencePort interfaces in the application layer capturing required operations (mark online/offline, query participants, stream updates).

Refactor PresenceService into an adapter implementing the new port, isolating Reactor sinks and Spring annotations.

Update controllers, agents, and WebSocket/SSE adapters to depend on PresencePort rather than the concrete service.

Add or adjust tests to cover presence updates via the port abstraction and validate backward compatibility.
<!-- SECTION:PLAN:END -->
