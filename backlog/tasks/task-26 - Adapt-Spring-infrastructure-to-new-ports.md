---
id: task-26
title: Adapt Spring infrastructure to new ports
status: To Do
assignee: []
created_date: '2025-10-25 10:35'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Once domain and application layers are in place, refactor the existing Spring WebFlux components to act as adapters implementing outbound ports and invoking inbound ports.

Scope:
- Update REST controllers, WebSocket handlers, and SSE endpoints to call application services rather than concrete implementations.
- Implement outbound adapters for the Reactor message bus, moderation event publisher, and AI provider that satisfy the defined ports.
- Ensure dependency injection wiring shifts to the new module boundaries (platform configuration assembling domain + application + adapters).

Out of scope: persistence backends or agent runtime changes; focus on wiring existing infrastructure to the new ports.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 All Spring beans use the new port interfaces, with adapters encapsulating Reactor, SSE, and external AI integration.
- [ ] #2 Integration tests or smoke tests confirm message publishing, moderation, and SSE streams still function end-to-end.
- [ ] #3 Documentation updated to show how adapters map to the new ports.
<!-- AC:END -->
