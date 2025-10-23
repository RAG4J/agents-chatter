---
id: task-7
title: Display active participants in the frontend header
status: Done
assignee:
  - assistant
created_date: '2025-10-23 17:03'
updated_date: '2025-10-23 22:53'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Update the Next.js chat UI to replace the static avatar group with dynamic participant chips that reflect the active presence list supplied by the backend. Subscribe to the presence feed, render initials/avatars with role styling, and update the header in real time as users join or leave.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Frontend consumes the presence API or websocket events and maintains a list of active participants.
- [x] #2 Header avatar group shows initials or avatars for each participant, handling roles (agent vs human) with distinct styling.
- [x] #3 Presence list updates in real time when participants appear/disappear; UI should fallback gracefully when backend presence is unavailable.
- [x] #4 Documentation or inline help explains how a user sets their displayed identity (e.g., query param, local config) and how the presence avatars are derived.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Implementation Plan:
1. Presence data plumbing
   - Consume the backend presence REST/SSE endpoints (`/api/presence`, `/api/presence/stream`) from the frontend.
   - Create a TypeScript model (`PresenceParticipant`) and a data access helper (`lib/api/presence.ts`).
2. Frontend presence state hook
   - Build a React hook (`usePresence`) that fetches the presence list, subscribes to the SSE feed, and maintains participant state with loading/error indicators.
   - Handle fallback when SSE is unavailable (polling or single fetch) and default to mock data if backend is unreachable.
3. Header UI integration
   - Update `MessageHeader` to accept participant data and render dynamic avatar chips with initials/role-based styling.
   - Display limited avatars (e.g., top 5) with overflow indicator and tooltips listing names.
4. Additional UI tweaks
   - Optionally show participant count/status text alongside the avatars.
   - Ensure responsiveness and accessible labels for screen readers.
5. Tests & verification
   - Add/update Jest/Vitest tests covering the hook (mocking fetch/SSE) and header component rendering with sample presence data.
   - Manually verify with the running backend that avatars update when participants connect/disconnect.
6. Documentation
   - Update frontend README explaining the presence feed, required environment variables, and current limitations (e.g., SSE fallback behaviour).
   - Note how to run backend + frontend together to see live presence updates.
<!-- SECTION:PLAN:END -->
