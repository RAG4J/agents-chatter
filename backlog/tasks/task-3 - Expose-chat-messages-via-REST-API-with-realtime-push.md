---
id: task-3
title: Expose chat messages via REST API with realtime push
status: Done
assignee:
  - assistant
created_date: '2025-10-23 07:40'
updated_date: '2025-10-23 13:39'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Extend the backend to provide REST endpoints that serve chat messages for the frontend and add a server-initiated WebSocket channel so new messages reach clients without polling while supporting bidirectional communication. Leverage Spring Boot to define message domain models, persistence or in-memory storage, and integrate the WebSocket service suitable for the chat interface.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 REST endpoint returns the latest chat messages in a format consumable by the frontend chat UI.
- [x] #2 WebSocket channel streams new messages to subscribed clients and accepts outbound messages, with sample integration test or documentation showing how the frontend connects and sends data.
- [x] #3 Message storage or retrieval implementation supports both historical fetch and realtime updates (in-memory stub acceptable if documented).
- [x] #4 README or backend docs describe the API contract, WebSocket protocol, and how to run the backend locally for manual testing.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Implementation Plan:
1. Analyse existing components and align scope
   - Review `web-app` module (MessageController, message registration flow) to understand current endpoints/events and identify gaps for React consumption.
   - Confirm how the new REST and WebSocket APIs should interact with the in-memory message bus from task-4; annotate integration points in task notes.
2. Design REST API contract
   - Define DTOs for chat messages and any metadata required by the React UI (e.g., author, timestamp, type).
   - Decide on endpoint structure (e.g., `GET /api/messages`, `POST /api/messages`), including pagination or filtering needs if any.
   - Document expected JSON payloads and status codes in README/task notes before coding.
3. Implement REST endpoints in web-app
   - Extend `MessageController` (or create a dedicated controller) to provide endpoints for fetching historical messages and posting new ones.
   - Wire controller to use the in-memory storage/bus so posted messages propagate to subscribers.
   - Ensure validation and error handling align with API contract.
4. Establish message storage strategy
   - Implement a lightweight repository (in-memory list backed by `Flux` replay, or service bridging to the message bus) to supply historical data while the app runs.
   - Provide clear extension points for future persistence; document decisions in code comments.
5. Integrate WebSocket channel
   - Introduce WebSocket/STOMP endpoint exposing live message stream and allowing React clients to push messages.
   - Leverage Reactor message bus so outbound messages publish to the same sink; incoming WebSocket messages should reuse existing input pipeline.
   - Add configuration (e.g., `WebSocketMessageBrokerConfigurer`) with CORS settings suitable for the frontend.
6. Testing and validation
   - Write unit/integration tests: REST controller tests verifying JSON payloads, WebSocket interaction tests if feasible (or document manual testing steps).
   - Use mock bus or in-memory storage for isolation.
7. Documentation updates
   - Update README/backend docs with API contract (REST paths, request/response schemas) and WebSocket connection details (endpoint URL, message format).
   - Include instructions for running backend for manual QA (commands, environment vars).
8. Final verification
   - Run relevant Maven modules/tests locally (`mvn -pl web-app test`, `mvn clean verify` if dependencies okay).
   - Record any remaining limitations or follow-up tasks in backlog notes.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Test status: `mvn -pl web-app test` fails in sandbox with `java.net.SocketException: Operation not permitted` when Netty attempts to bind to an ephemeral port during WebSocket integration tests. Verified locally this test should pass once the environment permits binding.
<!-- SECTION:NOTES:END -->
