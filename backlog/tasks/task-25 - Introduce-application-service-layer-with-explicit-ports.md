---
id: task-25
title: Introduce application service layer with explicit ports
status: To Do
assignee: []
created_date: '2025-10-25 10:35'
updated_date: '2025-10-25 18:55'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Add an `application-services` module (or package) that encapsulates use cases for publishing messages, coordinating conversations, moderation, and presence, exposing clear inbound/outbound interfaces.

Scope:
- Define inbound ports for message commands, moderation queries, presence queries, and agent orchestration.
- Extract existing logic from `ConversationCoordinator`, `MessageService`, `ModeratorService`, and presence handling into application services that depend only on domain abstractions.
- Specify outbound ports required for message bus, moderation event publishing, AI agent integrations, and presence storage (in-memory stub for now).

Out of scope: implementing new adapters or persistence; this task focuses on the application layer design and interface definitions.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Application services and port interfaces compile in isolation from Spring/WebFlux infrastructure.
- [ ] #2 Existing controllers/agents can be refactored to depend on the new ports (even if integration happens in a follow-up task).
- [ ] #3 Documentation or inline README describes the responsibilities of the new application layer and its ports.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Implementation Plan:
1. Inventory existing application logic
   - Catalogue current services (`ConversationCoordinator`, `MessageService`, `ModeratorService`, presence handling) and note responsibilities/views they cover.
   - Identify inbound entry points (REST, WebSocket, agents) and outbound dependencies (message bus, moderation events, AI providers, presence registry).
2. Design application layer structure
   - Define packages/modules under `application-services` (e.g., `org.rag4j.chatter.application.messages`, `...moderation`, `...presence`, `...agents`).
   - Draft interfaces for inbound ports (commands/queries) and outbound ports (bus, moderation publisher, AI client, presence store).
3. Extract application services
   - Move orchestration logic from current Spring components into application services that depend only on domain + port interfaces.
   - Ensure services handle use cases (publish message, evaluate moderation, update presence, coordinate agents) and remain framework-free.
4. Provide transitional wiring
   - Create configuration classes (in infrastructure modules) that instantiate application services and bind adapters to ports.
   - Update existing Spring beans to call the new ports while maintaining behaviour.
5. Documentation & verification
   - Add README or design doc describing the application layer layout and responsibilities.
   - Write targeted tests (unit or integration) validating application services independently of Spring, covering moderation + messaging flows.
<!-- SECTION:PLAN:END -->
