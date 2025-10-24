---
id: task-15
title: Design for limiting repetitive agent chatter
status: In Progress
assignee:
  - codex
created_date: '2025-10-24 09:34'
updated_date: '2025-10-24 09:46'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Create a solution design that prevents agents from repeatedly reacting to one another without adding new information. Consider mechanisms such as topic threading with reply depth limits and a moderator agent that filters low-value responses. The document should explain the selected approach, how it integrates with the existing system, and enumerate follow-up implementation work.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Design evaluates at least two mitigation strategies and selects a preferred option.
- [ ] #2 Document explains how the chosen approach fits into the current message flow and architecture.
- [ ] #3 Deliverable lists specific follow-up engineering tasks derived from the design.
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Context
- Multiple SubscriberAgent implementations listen to the shared Reactor-based `MessageBus`
- Agents respond to every inbound message they did not author, which can cascade into low-value loops (EchoAgent, themed agents, etc.)
- Message metadata today contains only `id`, `author`, `payload`, and `createdAt`

## Goals
- Reduce repetitive agent-to-agent chatter without muting useful contextual replies
- Maintain transparency for human users about why messages are accepted or dropped
- Avoid introducing external infrastructure (keep in-memory, Spring-centric solution)

## Non-goals
- Building full agent RL or ML-based moderation
- Persisting chat history beyond the existing in-memory storage

## Candidate Strategies

### Strategy A — Topic Threading + Reply Depth Limits
Track a conversation/thread identifier per message and increment a depth counter whenever agents reply within the same thread. Enforce a maximum agent reply depth (e.g. 2) to stop infinite loops.
- **Pros:** Deterministic; easy to tune; aligns with product need; stops loops even when payload changes slightly.
- **Cons:** Requires augmenting message metadata and maintaining thread state; assumes we can infer thread linkage when humans reply.

### Strategy B — Moderator Agent Filtering
Introduce a dedicated ModeratorAgent (or service) that receives candidate responses from other agents, scores them via lightweight heuristics (duplicate payload, repeated author pairs, cooldown windows), and only publishes replies that add value.
- **Pros:** Central place for heuristics; easier to iterate on rule set; can emit rationale to UI.
- **Cons:** Additional hop increases latency; care needed to avoid moderator becoming single point of failure; requires agent cooperation or message interception to prevent bypass.

### Strategy C — Per-Agent Cooldown Windows
Throttle each agent so it cannot post more than once within a configurable window (e.g. 10 seconds).
- **Pros:** Simple to implement; no metadata changes.
- **Cons:** Weak at preventing 2-agent ping-pong if window is small; may suppress legitimate rapid responses.

## Recommended Approach
Adopt Strategies A and B together: enrich messages with thread metadata plus a depth limiter, and gate all automated replies through a Moderator pipeline before publishing to the bus.
- Depth limiting offers hard guarantees against infinite loops.
- Moderator heuristics provide flexibility (duplicate detection, semantic filters, per-agent cooldown policy) without relying solely on depth counts.
- Cooldown strategy can be folded into the moderator rules as an additional signal when needed.

## High-Level Flow
1. `MessageService.publish(author, payload)` delegates to a new `ConversationCoordinator` that:
   - assigns/propagates `threadId` and `parentMessageId`
   - computes `agentReplyDepth` (human-originated messages reset depth)
2. `ConversationCoordinator` evaluates depth limits; if threshold exceeded for an agent-generated reply, it drops the message and logs the block (optionally notify presence stream).
3. For eligible agent replies, coordinator hands off to `ModeratorService` which runs heuristics (duplicate payload detection, agent cooldown, banlists).
4. Moderator either returns `APPROVE`, `EDIT`, or `REJECT`:
   - `APPROVE` → message forwarded to `MessageBus` via `MessageService`
   - `EDIT` (optional hook) → adjust payload and forward (future extensibility)
   - `REJECT` → message suppressed; moderator emits rationale event for transparency
5. Approved messages propagate through the Reactor `MessageBus` as today; agents continue to subscribe but now receive richer metadata to inform future decisions.

## Integration Details
- Extend `MessageEnvelope` with immutable metadata (thread id UUID, parent id, reply depth, origin type). Consider separate DTO to avoid breaking API until frontend adapts.
- Introduce `ConversationCoordinator` in `web.messages` package; wire it into `MessageService.publish` and the REST/WebSocket controller so human messages start new threads.
- Implement `ModeratorService` as a Spring component. Agents should no longer call `messageService.publish` directly; instead, expose `AgentPublisher` facade that routes through moderator.
- Update concrete agents (EchoAgent, themed agents) to leverage new facade. Optionally pass metadata into `messagePayload` method so agents can conditionally respond (e.g. skip if depth near limit).
- Surface moderation outcomes through presence/events (new SSE channel or enrich existing `/presence` updates) so UI can explain suppressed responses.

## Follow-up Tasks
1. Extend messaging domain objects and API contracts to support thread metadata and origin flags.
2. Build `ConversationCoordinator` and integrate it with REST/WebSocket ingress and agent publisher flow.
3. Implement `ModeratorService` with initial rule set (duplicate detection, agent cooldown, rationale logging) and add agent-facing publishing facade.
4. Update existing agents and tests to use the moderated pipeline; add coverage for depth limit and suppression paths.
5. Expose moderation telemetry to frontend (API and UI surfaces) so humans see when/why agent replies were suppressed.
<!-- SECTION:NOTES:END -->
