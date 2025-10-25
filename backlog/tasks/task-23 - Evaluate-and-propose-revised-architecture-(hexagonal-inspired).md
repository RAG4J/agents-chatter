---
id: task-23
title: Evaluate and propose revised architecture (hexagonal-inspired)
status: In Progress
assignee:
  - codex
created_date: '2025-10-25 08:38'
updated_date: '2025-10-25 08:44'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Revisit the current backend/frontend architecture and identify a modular approach that supports future growth.

Scope:
- Assess existing Spring-based backend structure (web-app, event-bus, agents) and identify coupling/pain points that hinder modularity.
- Explore suitability of hexagonal (ports & adapters) or similar architectures for the project, considering messaging, agent orchestration, and frontend integration.
- Produce an architecture proposal outlining recommended modules, boundaries, interfaces, and migration steps, ensuring alignment with current capabilities and future roadmap.

Out of scope: implementing the new architecture; this task focuses on analysis and design recommendations.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Deliverable includes a written architecture proposal comparing current state vs. recommended modular/hexagonal approach.
- [ ] #2 Proposal identifies key domain boundaries, adapter layers, and integration points aligned with existing features.
- [ ] #3 Document outlines phased migration steps or prerequisites to adopt the new architecture without disrupting active development.
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Current State Assessment
- **Modules**: `event-bus` (reactive message bus), `web-app` (monolithic Spring WebFlux service hosting REST/WebSocket endpoints, message coordination, moderation, agent orchestration), `frontend` (Next.js UI).
- **Coupling**: Domain logic (conversation coordination, moderation, presence) resides directly under `org.rag4j.chatter.web.*`, tightly bound to WebFlux controllers and Spring beans. Agents depend on `MessageService`/`AgentPublisher`, which combine application logic and infrastructure concerns.
- **Communication**: In-memory Reactor bus for intra-service messaging; agents call OpenAI via Spring AI directly from adapter classes; no persistence beyond in-memory state.
- **Boundaries**: No explicit domain module; application services, domain policies, and infrastructure (Reactor, Spring AI) intermingled. DTOs mirror internal records; message metadata added ad hoc.

## Pain Points & Risks
1. **No clear domain core**: Policies such as depth limiting, moderation rules, and presence live in web layer, limiting reuse or testing outside Spring context.
2. **Inconsistent ports**: Agents and external services (AI providers) are hard-coded dependencies; swapping providers or adding persistence requires touching multiple packages.
3. **Limited modularity**: Adding new delivery channels (e.g., gRPC, CLI) or agent types would duplicate logic because orchestration isn’t separated into application layer services.
4. **Testing friction**: Unit tests rely on Spring beans and Reactor sinks; lacking pure domain services makes behavioral testing heavier.
5. **Scalability constraints**: Single in-memory bus and service make horizontal scaling difficult; no abstraction for distributed messaging/storage.

## Recommended Architecture (Hexagonal-Inspired)
Adopt a layered modular structure with explicit domain, application, and infrastructure boundaries:

### 1. Modules / Gradle-style subprojects
- **core-domain**: Pure domain models and policies (Message, Thread, Agent, ModerationRules, Presence states). No Spring dependencies.
- **application-services**: Use cases orchestrating domain logic (PublishMessageService, ModerationService, AgentCoordinationService). Defines inbound/outbound ports as interfaces.
- **infrastructure-adapters**: Spring-specific adapters implementing ports (REST, WebSocket, SSE controllers; Reactor message bus adapter; AI providers; persistence; SSE event publisher).
- **agents**: Agent behaviours implemented against application ports; could be split per agent package or remain configurable modules.
- **frontend** (unchanged but can consume richer APIs).
- **platform** (optional) for wiring Spring Boot configuration, assembling beans from other modules.

### 2. Ports & Adapters
- **Inbound ports**: `MessageCommandPort` (publish message), `ModerationQueryPort`, `PresenceQueryPort`, `AgentTriggerPort`. Adapters: REST controllers, WebSocket handlers, CLI/testing utilities.
- **Outbound ports**: `MessageBusPort`, `MessageStorePort` (future persistence), `ModerationEventPublisherPort`, `AgentLLMPort`, `PresenceRepositoryPort`.
- **Application services** call outbound interfaces; infrastructure modules provide implementations (Reactor bus, SSE, Spring AI, in-memory stores).

### 3. Messaging/Persistence Strategy
- Keep Reactor bus as default adapter; define port so alternative (Kafka, Redis Streams) can plug in later.
- Introduce optional persistence adapter (Postgres, Mongo) backing message history & presence, still abstracted via ports.
- Moderation events flow through `ModerationEventPublisherPort`, enabling multiple subscribers (SSE, WebSocket, metrics).

### 4. Agent Architecture
- Split agent logic into domain-level behaviours (state machines, response strategies) consumed via `AgentServicePort`.
- Outbound adapter for LLM calls; interface supporting stubbed/local models.
- Agent scheduler/runner sits in application layer, triggered by bus events but independent of Spring specifics.

### 5. Frontend Considerations
- With stable REST/SSE contracts (e.g., `/api/moderation/events`, `/api/messages`), frontend remains unchanged but benefits from consistent metadata schemas defined in core domain.

## Migration Plan (Phased)
1. **Foundational module extraction**
   - Create `core-domain` module with existing records (`MessageEnvelope`, conversation metadata) and pure domain services (depth calculation, moderation policies).
   - Refactor `ConversationCoordinator` & `RuleBasedModeratorService` to depend on domain interfaces.
2. **Application layer introduction**
   - Move coordinator, moderation, presence orchestration into `application-services`, exposing interfaces for controllers.
   - Define inbound/outbound ports; adapt controllers to call application services.
3. **Adapter refactor**
   - Implement Reactor bus, SSE, AI provider as outbound adapters meeting new interfaces.
   - Adjust Spring configuration to wire ports/adapters.
4. **Agent refactoring**
   - Update agents to consume application ports (publish, moderation decisions). Introduce domain-level agent configuration to ease future agents.
5. **Optional persistence & scalability**
   - Introduce `MessageStorePort` implementation if persistence is needed; consider distributed bus implementation adapter.
6. **Testing improvements**
   - Add module-level unit tests for domain/application services without Spring context.

## Next Steps / Open Questions
- Decide whether to adopt `spring-modulith` or simple Maven multi-module packaging for enforced boundaries.
- Evaluate need for distributed messaging early (Kafka, RabbitMQ) if scaling is imminent.
- Determine how agents should be deployed long-term (embedded vs. separate service per agent) and align ports accordingly.
- Plan documentation updates for API contracts reflecting modular architecture.
- Consider introducing API versioning in application layer once contracts stabilize.
<!-- SECTION:NOTES:END -->
