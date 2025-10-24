---
id: task-16
title: Extend messaging contracts with conversation metadata
status: To Do
assignee: []
created_date: '2025-10-24 10:13'
updated_date: '2025-10-24 10:22'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Update the shared messaging domain objects and API payloads to carry the metadata required for repetitive-chatter mitigation.

Scope:
- Extend `MessageEnvelope` with immutable `threadId`, `parentMessageId`, `originType` (human/agent), and `agentReplyDepth` fields.
- Ensure REST and WebSocket responses surface the new metadata, we need to fix the frontend as well.
- Introduce validation/utilities for propagating thread identifiers when publishing messages.

Out of scope: changes to agent publishing logic or moderation flows beyond the contract updates.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 New metadata fields are available to both backend services and frontend consumers without regression to existing message history endpoints.
- [ ] #2 REST `GET /api/messages` and WebSocket payloads include the additional metadata (verified via tests or documented contract).
- [ ] #3 Unit or integration coverage added for serialization/deserialization of the enriched message documents.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Implementation Plan:
1. Inventory current messaging contracts
   - Review `MessageEnvelope`, `MessageService`, REST/WebSocket DTOs, and TypeScript `MessageDto` to confirm all touchpoints for author/payload/timestamp data.
   - Identify where new metadata should originate (temporary defaults vs. coordinator hookup later) to avoid breaking existing flows during this task.
2. Extend backend message model
   - Add `threadId`, `parentMessageId`, `originType`, and `agentReplyDepth` to `MessageEnvelope` (introducing supporting types such as a `MessageOrigin` enum).
   - Update constructors/factory methods (`MessageEnvelope.from`, `MessageService.publish`) to populate sensible defaults and ensure null-safety/validation.
3. Surface metadata through API payloads
   - Update `MessageDto` and any serialization layers so REST `GET /api/messages`, POST responses, and WebSocket broadcasts include the new fields.
   - Adjust controller/service code to map between envelope and DTO, maintaining backwards compatibility (e.g., optional JSON properties) where feasible.
4. Update frontend consumers
   - Expand `frontend` TypeScript `MessageDto` and `Message` types plus mapping helpers (`mapDtoToMessage`, message feed hooks) to understand the enriched payload.
   - Apply any minimal UI adjustments needed to safely ignore/display the new metadata without breaking existing rendering.
5. Add verification and documentation
   - Introduce unit tests covering serialization/deserialization of the updated DTOs on both backend (e.g., JSON contract tests) and frontend (TypeScript mapping).
   - Document the new fields in developer docs or inline comments and note default behaviors until the coordinator/moderator tasks provide real values.
<!-- SECTION:PLAN:END -->
