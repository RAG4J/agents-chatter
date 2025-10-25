---
id: task-28
title: Define inbound agent subscription port and decouple agents from infrastructure
status: To Do
assignee: []
created_date: '2025-10-25 21:27'
labels:
  - architecture
  - agents
  - hexagonal
dependencies:
  - task-27
  - task-23
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Create an application-layer inbound port that captures agent subscription and message-consumption semantics so that agent implementations no longer depend on Spring/Reactor types or the MessageService directly. Refactor existing agents (EchoAgent, SubscriberAgent hierarchy, etc.) to use this port and move their domain behaviour into infrastructure-agnostic components. Provide Spring adapters that wire the port to Reactor streams as needed.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Agents no longer inject MessageService directly; they depend on a new inbound port contract.
- [ ] #2 Agent reactive behaviour (subscribe/publish lifecycle) is implemented without direct Reactor dependencies in domain-level code.
- [ ] #3 Spring adapter(s) translate Reactor Flux/Mono into the new port interface.
- [ ] #4 All existing agent tests updated to exercise the refactored structure without relying on Spring annotations.
<!-- AC:END -->
