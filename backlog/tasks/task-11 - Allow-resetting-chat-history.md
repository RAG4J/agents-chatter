---
id: task-11
title: Allow resetting chat history
status: To Do
assignee:
  - Warp
created_date: '2025-10-23 22:52'
updated_date: '2025-10-26 10:47'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Provide a way to clear the current chat transcript (both on the backend message history and visible in the frontend) so users can restart the conversation fresh. Expose an API/command to wipe stored messages and update the UI with a control to trigger the reset.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Backend exposes an endpoint or command to clear stored chat messages and reset any in-memory history caches.
- [ ] #2 Frontend adds a “Clear conversation” action that invokes the backend reset and refreshes the UI state.
- [ ] #3 Confirm that after reset the message history is empty and new messages flow normally.
- [ ] #4 Document the reset functionality, including any cautionary notes (e.g., irreversible, affects all participants).
<!-- AC:END -->
