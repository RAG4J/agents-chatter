---
id: task-29
title: Introduce PresencePort and isolate presence tracking infrastructure
status: To Do
assignee: []
created_date: '2025-10-25 21:27'
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
