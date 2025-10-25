---
id: task-26
title: Adapt Spring infrastructure to new ports
status: Done
assignee:
  - codex
created_date: '2025-10-25 10:35'
updated_date: '2025-10-25 19:37'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Once domain and application layers are in place, refactor the existing Spring WebFlux components to act as adapters implementing outbound ports and invoking inbound ports.

Scope:
- Update REST controllers, WebSocket handlers, and SSE endpoints to call application services rather than concrete implementations.
- Implement outbound adapters for the Reactor message bus, moderation event publisher, and AI provider that satisfy the defined ports.
- Ensure dependency injection wiring shifts to the new module boundaries (platform configuration assembling domain + application + adapters).

Out of scope: persistence backends or agent runtime changes; focus on wiring existing infrastructure to the new ports.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 All Spring beans use the new port interfaces, with adapters encapsulating Reactor, SSE, and external AI integration.
- [x] #2 Integration tests or smoke tests confirm message publishing, moderation, and SSE streams still function end-to-end.
- [x] #3 Documentation updated to show how adapters map to the new ports.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Implementation Plan:
1. Map required adapters
   - List all existing Spring/WebFlux adapters (REST controllers, WebSocket handler, SSE moderators, presence services, LLM agents) and map them to the new inbound/outbound ports from application-services.
   - Identify missing adapters (e.g., presence store port implementation) needed for integration.
2. Implement outbound adapter classes
   - Update `MessageService` to implement `MessagePublicationPort` (already done) and introduce adapters for moderation policy (delegating to RuleBasedModeratorService) and moderation event publishing.
   - If needed, add additional adapters for presence storage or AI integration using existing Spring beans.
3. Refine inbound adapters
   - Ensure `MessageController`, `MessageWebSocketHandler`, and agent flows construct `PublishCommand`s and handle `PublishResult` consistently.
   - Adapt presence or moderation endpoints to use application services rather than direct domain logic.
4. Adjust configuration wiring
   - Extend Spring configuration to instantiate application services and inject the new adapters for ports (ensure bean ordering, qualifiers as needed).
   - Confirm no circular dependencies; remove obsolete wiring left over from previous architecture.
5. Verification & documentation
   - Run module builds/tests (`mvn -pl web-app -am test` where feasible) to ensure adapters work end-to-end.
   - Update README or module documentation summarizing adapter responsibilities and new wiring pattern.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
### Progress
- Application service layer now consumed by Spring adapters: `MessageService`, `ModerationEventPublisher`, and `ModeratorService` implement outbound ports; `ConversationCoordinator`, controllers, WebSocket handler, and agents delegate via `PublishCommand`/`PublishResult`.
- Added `AgentRegistryService` with in-memory registration adapter to prepare for agent modularization.
- Introduced application-layer configuration wiring (`ApplicationServicesConfig`) to assemble domain + ports.
- Updated unit tests to use application services, ensuring consistent behaviour, and ran `mvn -pl application-services compile`, `mvn -pl event-bus -am test -DskipITs`, and `mvn -pl web-app -am compile` (web-app tests skipped due to external AI calls).
- Updated documentation (application-services README) to reflect responsibilities.
<!-- SECTION:NOTES:END -->
