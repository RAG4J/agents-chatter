---
id: task-19
title: Update agents to use moderated publishing pipeline
status: To Do
assignee: []
created_date: '2025-10-24 10:14'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Refactor existing `SubscriberAgent` implementations to operate with the ConversationCoordinator and ModeratorService.

Scope:
- Replace direct `messageService.publish` calls with the new moderated `AgentPublisher` facade.
- Pass message metadata/context to agents so they can make depth- or thread-aware decisions before responding.
- Add automated tests (unit or integration) verifying that agents respect depth limits and moderator decisions (e.g., suppressed responses are not emitted).

Out of scope: frontend changes or new agent behaviors beyond moderation compliance.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 All current agents (Echo, StarTrek, StarWars, Football, Apeldoorn schedule) publish via the moderated pipeline.
- [ ] #2 Tests demonstrate that agents do not emit responses when depth limits or moderator rejections apply.
- [ ] #3 SubscriberAgent base class exposes necessary metadata/context without breaking existing agent overrides.
<!-- AC:END -->
