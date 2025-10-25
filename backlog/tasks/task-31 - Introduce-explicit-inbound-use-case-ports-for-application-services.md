---
id: task-31
title: Introduce explicit inbound use-case ports for application services
status: In Progress
assignee:
  - '@codex'
created_date: '2025-10-25 21:27'
updated_date: '2025-10-25 22:31'
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

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Inventory controllers, adapters, and tests currently depending on concrete application service classes (e.g., ConversationApplicationService, AgentRegistryService).

Design inbound port interfaces for key use cases (message publishing, agent registry/discovery, etc.) clearly separated in the application layer.

Update application-services module to expose the new interfaces (possibly moving implementations to package-private) and ensure wiring provides the concrete beans.

Refactor web adapters and agents to inject the new ports instead of concrete classes, adjusting configuration and tests.

Document the new port usage and ensure DI configuration and tests remain green.
<!-- SECTION:PLAN:END -->
