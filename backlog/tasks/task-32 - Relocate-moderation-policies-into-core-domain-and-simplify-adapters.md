---
id: task-32
title: Relocate moderation policies into core domain and simplify adapters
status: To Do
assignee: []
created_date: '2025-10-25 21:27'
labels:
  - architecture
  - moderation
  - hexagonal
dependencies:
  - task-23
  - task-31
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Move RuleBasedModeratorService (and related policy logic) from the web-app module into core-domain or a dedicated policies module so the business rules live outside Spring infrastructure. Ensure adapters in web-app simply wire the ModerationPolicyPort to the relocated implementation. Remove the redundant ModeratorService interface that currently extends ModerationPolicyPort.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 RuleBasedModeratorService resides in a non-Spring module (core-domain or new policies module) with no direct Spring/Reactor dependencies.
- [ ] #2 Web adapters inject the ModerationPolicyPort implementation via configuration without re-declaring interfaces.
- [ ] #3 Existing moderation tests still pass and run from the new module.
- [ ] #4 ModeratorService interface removed or reduced to an adapter-specific component without extending application ports.
<!-- AC:END -->
