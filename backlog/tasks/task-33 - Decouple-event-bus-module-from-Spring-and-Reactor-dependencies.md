---
id: task-33
title: Decouple event-bus module from Spring and Reactor dependencies
status: To Do
assignee: []
created_date: '2025-10-25 21:27'
labels:
  - architecture
  - event-bus
  - hexagonal
dependencies:
  - task-30
  - task-23
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Refactor the event-bus module so it no longer depends on Spring Boot starters or Reactor-specific infrastructure. Provide a clean implementation of the message bus abstraction that can be wired into Spring from the adapters module but remains framework-free. Adjust Maven configuration accordingly.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 event-bus/pom.xml no longer depends on Spring Boot starters or WebFlux; only plain Java/Reactor (if required) dependencies remain.
- [ ] #2 Message bus implementation exposes interfaces suitable for application ports without requiring Spring context.
- [ ] #3 web-app module provides configuration to adapt the event-bus implementation into Spring (if necessary).
- [ ] #4 Tests updated to cover the refactored event bus in isolation.
<!-- AC:END -->
