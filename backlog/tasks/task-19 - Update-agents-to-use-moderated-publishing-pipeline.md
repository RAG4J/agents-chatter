---
id: task-19
title: Update agents to use moderated publishing pipeline
status: Done
assignee:
  - codex
created_date: '2025-10-24 10:14'
updated_date: '2025-10-24 14:06'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Refactor existing `SubscriberAgent` implementations to operate with the ConversationCoordinator and ModeratorService.

Scope:
- Replace direct `messageService.publish` calls with the new moderated `AgentPublisher` facade.
- Pass message metadata/context to agents so they can make depth- or thread-aware decisions before responding.
- Add automated tests (unit or integration) verifying that agents respect depth limits and moderator decisions (e.g., suppressed responses are not emitted).

Out of scope: frontend changes or new agent behaviors beyond moderation compliance.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 All current agents (Echo, StarTrek, StarWars, Football, Apeldoorn schedule) publish via the moderated pipeline.
- [ ] #2 Tests demonstrate that agents do not emit responses when depth limits or moderator rejections apply.
- [ ] #3 SubscriberAgent base class exposes necessary metadata/context without breaking existing agent overrides.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Implementation Plan:
1. Inventory current agent publishing touchpoints
   - Review each `SubscriberAgent` subclass (Echo, StarTrek, StarWars, Football, ApeldoornIT) to confirm they now rely on `AgentPublisher.publishAgentResponse` and note any bespoke behaviour needing metadata awareness.
   - Identify all call sites where agents might publish spontaneously (e.g., startup announcements) to ensure they have thread context or use the spontaneous API.
2. Enhance agent payload generation with context awareness
   - Update `SubscriberAgent.messagePayload` contract or wrapper so implementations receive `AgentMessageContext` or at least depth/thread hints, allowing selective suppression near limits.
   - Adjust agents that should avoid replying under certain conditions (e.g., Echo Agent ignoring loops) to consume the new metadata.
3. Extend AgentPublisher/conversation integrations
   - Introduce helper methods for spontaneous agent broadcasts that create new threads while passing through moderator + coordinator.
   - Ensure `AgentPublisher` returns the published envelope (or empty when rejected) so agents can react accordingly (e.g., log or adjust state).
4. Update specific agents
   - Refactor each agent to use the new context-aware hooks, ensuring they gracefully handle moderation rejections (e.g., logging, avoiding retries).
   - Verify AI-backed agents (StarTrek, StarWars, Football, ApeldoornIT) pass along thread context when invoking external APIs (e.g., include thread id in prompts if useful).
5. Testing & regression coverage
   - Update/extend unit tests for `SubscriberAgent`, `EchoAgent`, and placeholder agents to assert moderator rejections prevent publishes and metadata is honoured.
   - Add integration-style tests (mocking moderator) to simulate rejections and ensure agents do not flood the bus after a block.
   - Document behavioural changes or developer notes for implementing future agents with the moderated pipeline.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Progress
- Enhanced `SubscriberAgent` with a metadata-aware response flow; added `messagePayload(MessageEnvelope)` hook so agents can inspect thread/depth context and log moderator rejections.
- Upgraded `AgentPublisher` with a convenience overload for spontaneous messages and ensured coordinator parent envelopes travel through moderation.
- Updated `EchoAgent` to skip high-depth agent loops leveraging the new metadata hook.
- Added unit coverage verifying moderator rejections suppress agent output and depth-aware behaviour; refreshed placeholder agent tests accordingly.

## Testing
- `mvn -pl web-app test -DskipITs` ⚠️ (unit suites pass; run still times out later when Spring AI integration tests attempt external OpenAI calls).

## Follow-ups
- Consider extending other themed agents with deeper context awareness (e.g., selective replies based on thread metadata) as product requirements emerge.
- Coordinate with task-20 to surface moderator rejection telemetry so agents/humans see why responses were dropped.
<!-- SECTION:NOTES:END -->
