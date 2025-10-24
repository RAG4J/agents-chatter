---
id: task-16
title: Extend messaging contracts with conversation metadata
status: In Progress
assignee:
  - codex
created_date: '2025-10-24 10:13'
updated_date: '2025-10-24 13:02'
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

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Progress
- Extended `MessageEnvelope` with conversation metadata (`threadId`, `parentMessageId`, `originType`, `agentReplyDepth`) plus helper factory for future coordinator use.
- Propagated metadata through REST and WebSocket payloads via enriched `MessageDto`; added backend unit tests covering mapping and JSON serialization defaults.
- Updated frontend message contracts, DTO mapping, and mock data to surface the new fields with sensible fallbacks; introduced Vitest coverage for mapping defaults.

## Testing
- `mvn -pl event-bus test` ✅
- `mvn -pl web-app -am test` ⚠️ fails because agent integration tests call external OpenAI API (blocked in sandbox) after module compilation succeeded; no compilation regressions observed.
- `npm test` (frontend) ⚠️ existing Vitest suites fail due to unresolved `@/` path alias configuration; new mapping tests covered by same run but currently blocked by the global alias issue.

## Next Steps
- Resolve Vitest alias configuration so frontend tests can run cleanly.
- Coordinate with moderation/conversation tasks to replace temporary defaults (threadId = id, origin = UNKNOWN for legacy paths) once new pipeline is implemented.
- Close task once review complete; downstream tasks (17-20) ready to leverage new metadata.
<!-- SECTION:NOTES:END -->
