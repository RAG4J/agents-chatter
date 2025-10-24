---
id: task-17
title: Introduce ConversationCoordinator for moderated publishing
status: In Progress
assignee:
  - codex
created_date: '2025-10-24 10:13'
updated_date: '2025-10-24 13:13'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Create a `ConversationCoordinator` component that owns thread propagation, reply depth tracking, and moderation entry points for all inbound messages.

Scope:
- Intercept message publication paths (REST controller, WebSocket handler, agents) so they route through the coordinator before hitting `MessageService`.
- Compute/maintain `threadId`, `parentMessageId`, and `agentReplyDepth` based on incoming metadata and message origin (human vs agent).
- Enforce configurable depth limits for agent-authored replies and drop/log messages that exceed the allowed depth.

Out of scope: actual moderator heuristics; agent-specific publishing facades.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 All message ingestion paths use the coordinator before calling MessageService.publish.
- [ ] #2 Depth limit configuration exists and blocks agent replies that exceed the threshold, with logging or telemetry for the drop.
- [ ] #3 Automated tests cover thread propagation and depth limiting logic.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Implementation Plan:
1. Design coordinator contract
   - Define an interface/class (e.g., `ConversationCoordinator`) responsible for ingesting publication requests with metadata (author, payload, optional context).
   - Specify a request object capturing source type (human/agent), optional parent message, and any provided thread identifiers.
   - Document expected outcomes (approved message envelope or rejection reason) for downstream components.
2. Implement thread/depth bookkeeping
   - Introduce coordinator state management (in-memory map or helper) that can derive/assign `threadId`, `parentMessageId`, and increment `agentReplyDepth` based on incoming requests.
   - Expose configuration properties for maximum agent depth and default thread expiration; add logging metrics when limits exceed.
   - Ensure method remains side-effect free aside from returning computed metadata for MessageService publish path.
3. Integrate with MessageService ingress
   - Update REST `MessageController` and WebSocket handler to call the coordinator with human-authored messages; persist/return the enriched envelope.
   - Replace direct `MessageService.publish` calls with coordinator flow that hands back the final `MessageEnvelope` for response serialization.
   - Confirm human-originated messages reset thread depth and start new threads when no parent specified.
4. Provide agent publishing entry point
   - Create an interim `AgentPublishingService` (or expose coordinator API) that agents can use until moderator service lands.
   - Update `SubscriberAgent` (and tests) to publish via the coordinator so agent metadata/limits apply consistently.
   - Ensure depth limit violations are logged and do not reach MessageService; surface optional event hook for future telemetry.
5. Testing and validation
   - Add unit tests covering coordinator thread propagation, depth limit enforcement, and metadata defaults.
   - Extend existing agent/message service tests to exercise the new pipeline (e.g., simulate agent replies exceeding depth, human restart of thread).
   - Update documentation or inline comments to explain the coordinator responsibilities and configuration knobs.
<!-- SECTION:PLAN:END -->
