---
id: task-5
title: Establish shared chat domain module
status: To Do
assignee: []
created_date: '2025-10-23 07:59'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Introduce a dedicated Maven module that holds chat message domain models, events, and utilities so both the Spring Boot web-app and the event bus share consistent types. Configure the module under the parent build, validate Java 21 compatibility, and publish artifacts other modules can depend on.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 `chat-domain` module is added to the parent Maven project with Java 21 compilation settings aligned to the parent.
- [ ] #2 Module exposes chat message, agent metadata, and event payload classes used by both the web-app and event-bus modules.
- [ ] #3 Unit tests cover basic serialization or validation logic where applicable, ensuring domain objects behave as expected.
- [ ] #4 Documentation (module README or parent README section) explains how other modules should depend on and evolve the shared domain types.
<!-- AC:END -->
