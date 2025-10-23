---
id: task-3
title: Expose chat messages via REST API with realtime push
status: To Do
assignee: []
created_date: '2025-10-23 07:40'
updated_date: '2025-10-23 07:50'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Extend the backend to provide REST endpoints that serve chat messages for the frontend and add a server-initiated WebSocket channel so new messages reach clients without polling while supporting bidirectional communication. Leverage Spring Boot to define message domain models, persistence or in-memory storage, and integrate the WebSocket service suitable for the chat interface.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 REST endpoint returns the latest chat messages in a format consumable by the frontend chat UI.
- [ ] #2 WebSocket channel streams new messages to subscribed clients and accepts outbound messages, with sample integration test or documentation showing how the frontend connects and sends data.
- [ ] #3 Message storage or retrieval implementation supports both historical fetch and realtime updates (in-memory stub acceptable if documented).
- [ ] #4 README or backend docs describe the API contract, WebSocket protocol, and how to run the backend locally for manual testing.
<!-- AC:END -->
