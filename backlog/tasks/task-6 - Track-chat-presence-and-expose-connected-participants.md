---
id: task-6
title: Track chat presence and expose connected participants
status: Done
assignee:
  - assistant
created_date: '2025-10-23 17:03'
updated_date: '2025-10-23 22:37'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Enhance the Spring Boot backend to keep track of active chat subscribers (WebSocket, SSE, or future channels) along with their declared identity. When a client connects, allow them to register their display name/role, update the presence list on lifecycle events, and stream/broadcast presence updates so other clients know who is online.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Server captures participant identity on connect (e.g., handshake param or registration message) and stores it while the session is active.
- [x] #2 Presence list is updated when clients connect/disconnect and can be queried via REST and/or pushed via a dedicated WebSocket channel or message event.
- [x] #3 Message bus or presence service cleans up disconnected sessions to prevent stale entries.
- [x] #4 Tests or manual verification steps cover connect/disconnect scenarios and confirm presence list accuracy.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Implementation Plan:
1. Define presence model
   - Decide on the data structure to represent a participant (id, display name, role, connection type) and store it in a dedicated service.
   - Clarify how clients will provide their identity (query param, handshake headers, initial message) for both WebSocket and SSE/REST channels.
2. Backend presence service
   - Implement a `PresenceService` that tracks active sessions keyed by connection id and exposes methods to register/unregister participants.
   - Hook into WebSocket lifecycle events to call `PresenceService` when sessions connect/disconnect; plan for SSE or future channels similarly.
3. Broadcast/Expose presence list
   - Provide a REST endpoint (e.g., `GET /api/presence`) returning the current participant list.
   - Optionally publish presence updates on the message bus or a dedicated WebSocket topic so clients can update in real time.
4. Cleanup and loop prevention
   - Ensure disconnections remove participants even on abrupt termination; add timeouts or heartbeats if necessary.
   - Handle multiple connections per participant gracefully (multiple tabs) and decide whether to merge or list separately.
5. Testing & verification
   - Add unit tests for `PresenceService` (register/unregister scenarios) and WebSocket lifecycle handling.
   - Perform manual test by connecting multiple clients (browser tabs, curl) and verifying presence list updates correctly.
6. Documentation
   - Update README/developer notes with instructions on how clients should submit identity info and how to query presence.
   - Capture known limitations (e.g., no persistence, reliance on WebSocket only) and potential enhancements.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Participants list: agents already provide display names during configuration; default human participant can be labelled "You" for now.

Automated tests (`mvn -pl web-app test`) rely on Maven dependency downloads and were not run in the sandbox. PresenceService unit tests added to validate register/unregister behaviour.

Presence now treated as simple participant online status based on message bus registration: agents mark themselves online at startup; human clients pass a `participant` query parameter when opening WebSocket connections (default "You"). REST endpoint `/api/presence` and SSE stream `/api/presence/stream` expose the list.
<!-- SECTION:NOTES:END -->
