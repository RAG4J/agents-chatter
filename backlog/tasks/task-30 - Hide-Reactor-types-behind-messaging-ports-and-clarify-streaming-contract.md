---
id: task-30
title: Hide Reactor types behind messaging ports and clarify streaming contract
status: To Do
assignee: []
created_date: '2025-10-25 21:27'
updated_date: '2025-10-25 22:17'
labels:
  - architecture
  - messaging
  - hexagonal
dependencies:
  - task-28
  - task-23
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Refine the messaging-related ports so the application layer does not expose Reactor classes. Introduce an outbound port (e.g., AgentMessageStreamPort) or adjust MessagePublicationPort/AgentMessagingPort to express streaming through domain-friendly abstractions, and provide adapters in web-app that convert to Reactor Flux/Mono. Update MessageService accordingly.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 No application-service class exposes Reactor types directly; ports use domain or Java types.
- [ ] #2 MessageService (or replacement adapter) implements the updated port(s) and confines Reactor usage to infrastructure layer.
- [ ] #3 Embedded agents and controllers consume the new abstractions without referencing Reactor APIs.
- [ ] #4 Regression tests updated to cover the revised contracts.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Catalogue all places where Reactor types (Flux/Mono) bleed into application services or ports, especially in messaging flows.

Design an updated set of messaging ports (e.g., introduce a streaming abstraction or adjust existing AgentMessagingPort/MessagePublicationPort) that use domain or JDK types.

Provide transitional adapters within web-app (and other adapters) that wrap Reactor streams to the new abstractions without breaking existing behaviour.

Refactor application services and agents to depend solely on the new port contracts, ensuring no Reactor imports remain in application-services.

Update tests and documentation to reflect the new messaging port design, adding coverage for the converted stream adapters.
<!-- SECTION:PLAN:END -->
