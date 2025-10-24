---
id: task-17
title: Introduce ConversationCoordinator for moderated publishing
status: To Do
assignee: []
created_date: '2025-10-24 10:13'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Create a `ConversationCoordinator` component that owns thread propagation, reply depth tracking, and moderation entry points for all inbound messages.

Scope:
- Intercept message publication paths (REST controller, WebSocket handler, agents) so they route through the coordinator before hitting `MessageService`.
- Compute/maintain `threadId`, `parentMessageId`, and `agentReplyDepth` based on incoming metadata and message origin (human vs agent).
- Enforce configurable depth limits for agent-authored replies and drop/log messages that exceed the allowed depth.

Out of scope: actual moderator heuristics; agent-specific publishing facades.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 All message ingestion paths use the coordinator before calling MessageService.publish.
- [ ] #2 Depth limit configuration exists and blocks agent replies that exceed the threshold, with logging or telemetry for the drop.
- [ ] #3 Automated tests cover thread propagation and depth limiting logic.
<!-- AC:END -->
