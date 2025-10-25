---
id: task-31
title: Introduce explicit inbound use-case ports for application services
status: To Do
assignee: []
created_date: '2025-10-25 21:27'
labels:
  - architecture
  - hexagonal
  - application-layer
dependencies:
  - task-23
  - task-25
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Define inbound interfaces for key application services (e.g., conversation publishing, agent registry) so adapters depend on contracts rather than concrete classes. Refactor controllers and other adapters to inject the new ports, keeping implementation classes internal to the application-services module.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Conversation publishing endpoints depend on a new inbound port interface instead of ConversationApplicationService directly.
- [ ] #2 Agent registry endpoints/agents depend on a dedicated use-case interface located in the application layer.
- [ ] #3 Application-service implementation classes remain package-private where possible, with tests updated accordingly.
- [ ] #4 Document the new inbound ports to guide future adapter implementations.
<!-- AC:END -->
