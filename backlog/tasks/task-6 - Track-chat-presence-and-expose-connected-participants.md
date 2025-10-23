---
id: task-6
title: Track chat presence and expose connected participants
status: To Do
assignee: []
created_date: '2025-10-23 17:03'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Enhance the Spring Boot backend to keep track of active chat subscribers (WebSocket, SSE, or future channels) along with their declared identity. When a client connects, allow them to register their display name/role, update the presence list on lifecycle events, and stream/broadcast presence updates so other clients know who is online.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Server captures participant identity on connect (e.g., handshake param or registration message) and stores it while the session is active.
- [ ] #2 Presence list is updated when clients connect/disconnect and can be queried via REST and/or pushed via a dedicated WebSocket channel or message event.
- [ ] #3 Message bus or presence service cleans up disconnected sessions to prevent stale entries.
- [ ] #4 Tests or manual verification steps cover connect/disconnect scenarios and confirm presence list accuracy.
<!-- AC:END -->
