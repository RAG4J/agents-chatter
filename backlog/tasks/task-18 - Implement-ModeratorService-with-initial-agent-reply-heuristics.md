---
id: task-18
title: Implement ModeratorService with initial agent reply heuristics
status: To Do
assignee: []
created_date: '2025-10-24 10:14'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Add a `ModeratorService` that evaluates candidate agent messages using rule-based heuristics before they are published.

Scope:
- Provide an approval API (e.g., `evaluate(agentName, messageContext) -> decision`) supporting APPROVE/REJECT (and optional EDIT) outcomes.
- Implement baseline heuristics: duplicate payload suppression, per-agent cooldown windows, and repeated agent pair loop detection.
- Emit rationale metrics/logs/events when rejections occur to support transparency requirements.
- Introduce an `AgentPublisher` facade for agents to submit replies through the moderator instead of calling `MessageService` directly.

Out of scope: advanced semantic analysis or ML scoring.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 ModeratorService decisions are enforced for all automated agent publications via the new AgentPublisher facade.
- [ ] #2 At least three heuristics (duplicate payload, cooldown, agent pair loop) are implemented and unit-tested.
- [ ] #3 Moderation rejections produce structured logs or events containing the suppressed reason.
<!-- AC:END -->
