---
id: task-7
title: Display active participants in the frontend header
status: To Do
assignee: []
created_date: '2025-10-23 17:03'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Update the Next.js chat UI to replace the static avatar group with dynamic participant chips that reflect the active presence list supplied by the backend. Subscribe to the presence feed, render initials/avatars with role styling, and update the header in real time as users join or leave.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Frontend consumes the presence API or websocket events and maintains a list of active participants.
- [ ] #2 Header avatar group shows initials or avatars for each participant, handling roles (agent vs human) with distinct styling.
- [ ] #3 Presence list updates in real time when participants appear/disappear; UI should fallback gracefully when backend presence is unavailable.
- [ ] #4 Documentation or inline help explains how a user sets their displayed identity (e.g., query param, local config) and how the presence avatars are derived.
<!-- AC:END -->
