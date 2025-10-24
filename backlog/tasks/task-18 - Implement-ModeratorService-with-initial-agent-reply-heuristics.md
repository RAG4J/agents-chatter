---
id: task-18
title: Implement ModeratorService with initial agent reply heuristics
status: Done
assignee:
  - codex
created_date: '2025-10-24 10:14'
updated_date: '2025-10-24 13:55'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Add a `ModeratorService` that evaluates candidate agent messages using rule-based heuristics before they are published.

Scope:
- Provide an approval API (e.g., `evaluate(agentName, messageContext) -> decision`) supporting APPROVE/REJECT (and optional EDIT) outcomes.
- Implement baseline heuristics: duplicate payload suppression, per-agent cooldown windows, and repeated agent pair loop detection.
- Emit rationale metrics/logs/events when rejections occur to support transparency requirements.
- Introduce an `AgentPublisher` facade for agents to submit replies through the moderator instead of calling `MessageService` directly.

Out of scope: advanced semantic analysis or ML scoring.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 ModeratorService decisions are enforced for all automated agent publications via the new AgentPublisher facade.
- [ ] #2 At least three heuristics (duplicate payload, cooldown, agent pair loop) are implemented and unit-tested.
- [ ] #3 Moderation rejections produce structured logs or events containing the suppressed reason.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Implementation Plan:
1. Define moderation contracts
   - Introduce `ModeratorService` interface with an `evaluate(agentName, messageContext)` method returning APPROVE/REJECT (plus optional metadata).
   - Create a `ModerationDecision` model capturing status, rationale, and any transformed payload.
   - Outline a `AgentMessageContext` record carrying payload, thread metadata, agent depth, and previous history references.
2. Implement rule engine scaffolding
   - Provide baseline rule implementations for duplicate payload blocking, per-agent cooldown windows, and reciprocal agent loop detection.
   - Add configuration properties controlling cooldown duration, duplicate matching strategy (exact vs. case-insensitive), and loop detection window size.
   - Compose rules inside `ModeratorService` (e.g., ordered list or predicate chain) and short-circuit on the first rejection.
3. Integrate with ConversationCoordinator
   - Extend the coordinator to call `ModeratorService` after depth checks but before publishing.
   - Ensure accepted decisions can optionally mutate payload, while rejections log rationale and prevent publication.
   - Wire moderator into `AgentPublisher` so all agent submissions flow through the combined depth + rule checks.
4. Persistence / state tracking
   - Store recent agent messages per thread to support duplicate/loop detection; leverage existing coordinator thread state or introduce a lightweight cache with eviction.
   - Track per-agent last sent timestamps for cooldown enforcement (use `ConcurrentHashMap` with `Instant` values and configurable expiry).
5. Testing and observability
   - Add unit tests covering each heuristic and the overall decision pipeline (approve/reject cases, payload edits if supported).
   - Update existing agent tests (Echo, placeholder) to ensure moderator interaction is mocked/stubbed when needed; add new tests verifying blocked responses don’t reach the bus.
   - Emit structured logs/metrics on moderation outcomes for future telemetry surfaces (to be consumed by task-20).
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Progress
- Added moderation contracts (`AgentMessageContext`, `ModerationDecision`, `ModeratorService`) and implemented `RuleBasedModeratorService` driven by configurable cooldown, duplicate, and loop heuristics.
- Registered the moderator via `ModeratorConfiguration` with `@ConfigurationProperties` support for tuning defaults.
- Extended `ConversationCoordinator` to run moderator checks prior to publishing and enhanced `PublishRequest` to carry parent metadata; integrated decisions into `AgentPublisher`.
- Updated REST and WebSocket handlers plus all `SubscriberAgent` implementations/tests to flow through the moderated pipeline.
- Added targeted unit coverage for the moderator heuristics and refreshed coordinator/agent tests to exercise the new behaviour.

## Testing
- `mvn -pl web-app test` ⚠️ builds and runs unit suites; fails later when Spring AI integration tests invoke external OpenAI APIs (sandbox restriction).
- `mvn -pl event-bus install` ✅ (ensures MessageEnvelope changes propagated earlier remain compatible).

## Follow-ups
- Surface moderation rejections via explicit telemetry feeds for the frontend (task-20).
- Consider persistence/eviction strategy for moderator thread state to avoid unbounded maps in long-lived sessions.
- Evaluate whether additional heuristics (semantic similarity, cooldown per thread) are needed once real traffic is observed.
<!-- SECTION:NOTES:END -->
