---
id: task-24
title: Extract core-domain module for messaging and moderation policies
status: To Do
assignee: []
created_date: '2025-10-25 10:34'
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
