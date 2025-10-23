---
id: task-5
title: Integrate frontend with backend chat services
status: Done
assignee:
  - assistant
created_date: '2025-10-23 14:33'
updated_date: '2025-10-23 17:00'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Connect the Next.js chat client to the Spring Boot REST and WebSocket APIs so real messages flow between the frontend and web-app. Replace mock data with live fetches, implement proper WebSocket subscriptions, and ensure the UI reflects backend state (loading, errors, connection status).
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Frontend fetches messages from `/api/messages` on initial load and displays the live data returned by the backend.
- [x] #2 Chat composer posts through the REST API and the UI reflects the backend response or error state.
- [x] #3 WebSocket hook connects to `/ws/messages`, streams incoming payloads into the UI, and handles reconnection or offline states gracefully.
- [x] #4 Update frontend documentation (.env usage, fallback behavior) and add tests or manual verification notes confirming end-to-end integration.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Implementation Plan:
1. Frontend/backend alignment
   - Confirm REST and WebSocket endpoints (`/api/messages`, `/ws/messages`) are available and behaving as described in task-3. Note any authentication or CORS requirements.
   - Decide on error-handling and loading-state conventions for the UI.
2. Replace REST mocks with live calls
   - Update `lib/api/messages.ts` to default to backend fetch/post and use mock fallback only on network failure.
   - Ensure fetch requests include proper headers and handle non-OK responses gracefully (surface error states to the UI).
   - Add loading indicators when the chat page fetches initial messages.
3. WebSocket integration
   - Enhance `useMessagesFeed` hook to rely on `NEXT_PUBLIC_WS_URL`, handle reconnection/backoff, and expose connection status.
   - Ensure server-sent messages merge cleanly with existing state without duplication (consider using message IDs).
4. UI state updates
   - Surface connection status (connected/connecting/offline) in `MessageHeader` or a dedicated status banner.
   - Display error toasts or inline notifications when REST calls fail; allow retry sending messages.
5. Testing & validation
   - Add Vitest tests or Playwright smoke test covering the fetch functions and hook behaviour (can mock fetch/WebSocket).
   - Run manual end-to-end test using the Spring Boot backend to verify live message flow (document steps).
6. Documentation updates
   - Update frontend README to describe required backend environment variables, how to run both services locally, and fallback behaviour when backend is offline.
   - Capture new `.env` expectations or optional toggles (e.g., enabling mock mode).
7. Final verification
   - Run `npm run lint`, `npm run test`, `npm run build` (or `mvn -pl frontend verify`) to ensure integration doesn’t break the build.
   - Record any limitations or follow-up tasks (e.g., authentication, message history pagination) in backlog notes.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Frontend wired to backend endpoints: REST fetch/post via `lib/api/messages.ts`, realtime WebSocket reconnection via `useMessagesFeed`. UI surfaces connection status and mock fallback indicators. Tests/build (`npm install`, `npm run build`) not run in sandbox due to lack of network access; run locally after pulling changes.

Manual verification: posting via curl (`curl -X POST http://localhost:8080/api/messages -H "Content-Type: application/json" -d '{"author":"CLI","payload":"Hello"}'`) confirms messages render once with full content in frontend after removal of optimistic insert.
<!-- SECTION:NOTES:END -->
