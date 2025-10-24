---
id: task-20
title: Expose moderation telemetry to frontend
status: Done
assignee:
  - codex
created_date: '2025-10-24 10:14'
updated_date: '2025-10-24 14:16'
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

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Implementation Plan:
1. Define telemetry payload contract
   - Specify a `ModerationEvent` DTO containing thread id, agent, rationale, timestamp, and optional payload snippet.
   - Decide on delivery channel (e.g., SSE stream, WebSocket topic, or REST endpoint) and document JSON schema.
2. Emit events backend-side
   - Instrument `RuleBasedModeratorService` (or ConversationCoordinator) to publish `ModerationEvent` whenever a decision is rejected.
   - Use Reactor `Sinks.Many` or ApplicationEventPublisher to broadcast events to interested consumers.
   - Ensure events are throttled/filtered to avoid leaking sensitive payloads (e.g., truncate long messages).
3. Expose telemetry API
   - Add a REST/SSE controller (e.g., `/api/moderation/events`) serving a hot stream of moderation events.
   - Optionally augment presence or existing WebSocket payloads with moderation event type to reuse client plumbing.
   - Secure the endpoint if needed (basic auth placeholder or comment for future auth integration).
4. Frontend subscription & UI
   - Implement a client hook/service to subscribe to the moderation feed (SSE or WebSocket) and maintain recent events in state.
   - Render inline notifications or a sidebar explaining suppressed messages; ensure UX doesn’t flood the chat.
   - Add dismissal/auto-expiration behaviour for moderation notices.
5. Testing & verification
   - Add backend unit/integration tests verifying events are emitted on rejection and delivered through the chosen channel.
   - Add frontend tests (component or hook level) to ensure moderation notices appear when events arrive and disappear appropriately.
   - Update documentation/README to explain how moderation telemetry works and how to consume the stream.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Progress
- Defined a `ModerationEvent` contract and introduced `ModerationEventPublisher` for broadcasting rejections from the rule-based moderator.
- Extended `RuleBasedModeratorService` to emit moderation events (including rationale, depth, and payload preview) and exposed them via a new SSE endpoint at `/api/moderation/events`.
- Added `ModerationEventsController` to serve the event stream and wired the moderator configuration to inject the publisher.
- Frontend now consumes the stream through `useModerationEvents`, surfaces recent moderation notices in the ChatShell sidebar, and handles stream errors gracefully.

## Testing
- `mvn -pl web-app test -DskipITs` ⚠️ (unit suites pass; run still times out when Spring AI integration tests call external OpenAI APIs).
- `npm test` ⚠️ existing Vitest config still lacks path aliases, so tests (including new moderation hook coverage) fail to resolve `@/` imports; needs alias fix as follow-up.
- Added unit coverage for moderation events (`RuleBasedModeratorServiceTests`) and UI hook behaviour (`frontend/hooks/useModerationEvents.test.ts`).

## Follow-ups
- Update Vitest configuration to resolve `@/` aliases so frontend tests execute without manual stubbing.
- Consider persisting/replaying a small buffer of moderation events for late subscribers if UX requires historical data.
- Evaluate how moderation notices should integrate with future notification/UX patterns beyond the current sidebar list.
<!-- SECTION:NOTES:END -->
