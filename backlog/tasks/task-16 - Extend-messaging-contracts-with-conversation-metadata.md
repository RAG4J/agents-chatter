---
id: task-16
title: Extend messaging contracts with conversation metadata
status: To Do
assignee: []
created_date: '2025-10-24 10:13'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Update the shared messaging domain objects and API payloads to carry the metadata required for repetitive-chatter mitigation.

Scope:
- Extend `MessageEnvelope` (or add a new DTO) with immutable `threadId`, `parentMessageId`, `originType` (human/agent), and `agentReplyDepth` fields.
- Ensure REST and WebSocket responses surface the new metadata without breaking existing clients (introduce versioning or optional fields as needed).
- Introduce validation/utilities for propagating thread identifiers when publishing messages.

Out of scope: changes to agent publishing logic or moderation flows beyond the contract updates.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 New metadata fields are available to both backend services and frontend consumers without regression to existing message history endpoints.
- [ ] #2 REST `GET /api/messages` and WebSocket payloads include the additional metadata (verified via tests or documented contract).
- [ ] #3 Unit or integration coverage added for serialization/deserialization of the enriched message documents.
<!-- AC:END -->
