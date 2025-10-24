---
id: task-20
title: Expose moderation telemetry to frontend
status: To Do
assignee: []
created_date: '2025-10-24 10:14'
updated_date: '2025-10-24 14:06'
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
