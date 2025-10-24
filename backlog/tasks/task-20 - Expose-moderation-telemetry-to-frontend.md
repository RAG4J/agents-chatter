---
id: task-20
title: Expose moderation telemetry to frontend
status: To Do
assignee: []
created_date: '2025-10-24 10:14'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Provide visibility into moderated agent responses so users understand when and why messages were suppressed.

Scope:
- Define a backend event or API surface (e.g., SSE stream or expanded presence payload) that publishes moderation decisions including agent, rationale, and timestamp.
- Extend the frontend to subscribe/render moderation notices in the chat UI without overwhelming the conversation flow.
- Document the contract for moderation events and ensure it is covered by automated tests.

Out of scope: designing new notification UX beyond basic chat context messaging.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Backend exposes a documented endpoint or channel delivering moderation events with rationale details.
- [ ] #2 Frontend displays moderation notices in the chat UI (or adjacent panel) so humans see suppressed responses.
- [ ] #3 Tests cover the backend event contract and frontend rendering logic.
<!-- AC:END -->
