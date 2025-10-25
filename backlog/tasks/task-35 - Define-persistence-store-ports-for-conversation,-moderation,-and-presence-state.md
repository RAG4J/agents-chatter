---
id: task-35
title: >-
  Define persistence store ports for conversation, moderation, and presence
  state
status: To Do
assignee: []
created_date: '2025-10-25 21:28'
labels:
  - architecture
  - persistence
  - hexagonal
dependencies:
  - task-29
  - task-32
  - task-33
priority: low
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Identify in-memory state currently held by MessageService, RuleBasedModeratorService, and PresenceService, and introduce application-layer repository/store ports describing the required persistence operations. Provide default in-memory adapters and adjust services to depend on the new ports, enabling future backing stores (e.g., database, Redis).
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Document the state each component manages and define corresponding repository/store interfaces in the application layer.
- [ ] #2 MessageService, RuleBasedModeratorService, and Presence handling code depend on the new ports instead of direct in-memory collections.
- [ ] #3 Provide default in-memory adapter implementations with tests demonstrating behaviour.
- [ ] #4 Outline follow-up work (if any) to plug in durable stores.
<!-- AC:END -->
