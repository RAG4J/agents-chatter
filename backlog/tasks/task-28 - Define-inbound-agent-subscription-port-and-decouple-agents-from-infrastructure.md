---
id: task-28
title: Define inbound agent subscription port and decouple agents from infrastructure
status: In Progress
assignee:
  - '@codex'
created_date: '2025-10-25 21:27'
updated_date: '2025-10-25 21:42'
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

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Audit existing agent classes to identify framework-specific dependencies (MessageService, Reactor Flux/Mono, Spring annotations).

Design an inbound agent subscription port describing subscription lifecycle and message delivery using framework-agnostic types.

Refactor agent domain logic to depend on the new port, adjusting AgentPublisher/AgentRegistry interactions and removing direct infrastructure dependencies.

Implement Spring/Reactor adapters that bridge the new port to the message bus and registration infrastructure so embedded agents continue to run.

Update and expand agent-related tests to validate the refactored architecture without Spring-managed behaviour.
<!-- SECTION:PLAN:END -->
