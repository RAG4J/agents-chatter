---
id: task-24
title: Extract core-domain module for messaging and moderation policies
status: To Do
assignee: []
created_date: '2025-10-25 10:34'
updated_date: '2025-10-25 17:39'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Create a standalone Maven module (e.g., `core-domain`) that houses the pure domain model and rules used across the system.

Scope:
- Move message-related value objects (envelope metadata, moderation events, thread identifiers) and associated business rules (depth limits, moderation heuristics) into the new module with no Spring dependencies.
- Define domain services or utilities for depth calculation, moderation decision inputs, and agent identity handling.
- Adjust existing modules to depend on the new domain artifact while keeping behaviour identical.

Out of scope: introducing persistence or changing existing application/module wiring beyond the necessary dependency adjustments.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 `core-domain` module builds independently without Spring or Reactor dependencies.
- [ ] #2 Existing web/app modules compile against the extracted domain types without behavioural regressions.
- [ ] #3 Basic unit tests cover depth calculation and moderation rule primitives within the new module.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Implementation Plan:
1. Define module structure
   - Add a new Maven module `core-domain` to the parent `pom.xml` with minimal dependencies (JUnit/Jupiter for tests only).
   - Set up package namespace `org.rag4j.chatter.domain` (or similar) to host domain types.
2. Move foundational value objects
   - Relocate `MessageEnvelope`, `ModerationEvent`, and related metadata/value types from `event-bus`/`web-app` into `core-domain`, adapting imports accordingly.
   - Replace Reactor/Spring-specific annotations with plain Java constructs; ensure records/immutability preserved.
3. Extract domain policies/utilities
   - Isolate depth calculation, moderation decision inputs, and any rule helpers into domain services or utilities that operate without Spring/Reactor.
   - Provide clear interfaces or static factories for future application layer use.
4. Update dependent modules
   - Adjust `event-bus` and `web-app` to depend on `core-domain`, removing duplicate definitions.
   - Ensure no circular dependencies; verify compilation by updating imports and module dependencies.
5. Add domain-level tests & docs
   - Create unit tests in `core-domain` covering moderation depth rules and basic value object invariants.
   - Update module README or inline docs explaining the purpose of `core-domain` and guidelines for keeping it framework-free.
<!-- SECTION:PLAN:END -->
