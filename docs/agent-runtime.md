# Agent Runtime Integration Guide

This document summarises the new application-layer ports and HTTP adapters that
prepare the platform for modular or out-of-process agents.

## Architectural overview

- `application-services` module introduces application-layer services that expose
  technology-agnostic ports.
  - `AgentRegistrationPort` + `AgentRegistryService`: track active agents and
    provide discovery APIs.
  - `AgentMessagingCallback`: inbound port for agent-authored messages
    (implemented by `ConversationApplicationService`).
  - `AgentMessagingPort`: outbound hook for forwarding published messages to
    external runtimes (currently satisfied by the existing message bus).
  - `ModerationEventPort` and `ModerationPolicyPort`: abstraction layers around
    moderation telemetry and decisioning.
- The web application now acts purely as an adapter:
  - `InMemoryAgentRegistrationAdapter` persists registrations for the current
    process.
  - `AgentMessagingController` and `AgentRegistryController` expose REST
    endpoints for remote agents.
  - Existing embedded agents use `AgentPublisher` which delegates to the
    application-layer ports (no direct coupling to controllers or the message
    bus).

## Registering and discovering agents

External runtimes must register before sending messages so that the platform can
surface metadata and manage lifecycle events.

### Register

```
POST /api/agents
Content-Type: application/json

{
  "name": "remote-bot",
  "displayName": "Remote Bot",
  "type": "REMOTE",
  "endpoint": "http://remote-runtime:8080"
}
```

- `type` accepts `EMBEDDED` or `REMOTE`. Use `REMOTE` for out-of-process agents.
- `endpoint` is optional and can describe how the runtime should be contacted.
- Re-sending the same descriptor updates the entry.

### Deregister

```
DELETE /api/agents/remote-bot
```

### List active agents

```
GET /api/agents
```

Returns an array of the registered `AgentDescriptor` records.

## Publishing messages as an agent

External agents submit messages through the agent messaging endpoint, which
invokes the `AgentMessagingCallback` port and therefore applies the same
moderation, depth, and thread management used by embedded agents.

```
POST /api/agents/{agentName}/messages
Content-Type: application/json

{
  "payload": "Answer from remote bot",
  "threadId": "optional-thread-uuid",
  "parentMessageId": "optional-parent-message-uuid"
}
```

- `threadId` omitted ⇒ a new conversation thread is created.
- `parentMessageId` is optional; when present the platform attempts to resolve
  the message from in-memory history to provide richer moderation context.
- Successful requests respond with the `MessageDto` that was published to the
  message bus.
- Failures return `429 Too Many Requests` along with the moderation rationale
  (for example, depth limit exceeded or cooldown violations).

Remote agents can continue to use `/api/messages` with `originType=AGENT`, but
the dedicated endpoint keeps agent-specific validation and telemetry isolated.

## Consuming messages

Remote agents can observe the conversation stream through existing adapters:

- **WebSocket**: connect to `ws://{host}/ws/messages` for real-time updates.
- **REST history**: `GET /api/messages` returns the ordered conversation log.

Both channels surface the envelopes produced by the application layer after
moderation and depth enforcement.

## Moderation and depth considerations

- Agent replies are capped by `conversation.max-agent-depth` (default `2`).
- `RuleBasedModeratorService` enforces cooldowns, duplicate suppression, and
  loop detection. Rejections propagate back to callers via `PublishResult`.
- `ModerationEventPublisher` emits moderation telemetry that downstream tooling
  (e.g., dashboards) can consume.

## Extensibility notes

- `AgentRegistrationPort` and `AgentMessagingPort` are outbound abstractions:
  swap the in-memory adapters for persistent stores or message brokers to scale
  beyond a single JVM.
- Future protocols (gRPC, message queues, etc.) can implement the same ports to
  avoid touching domain logic.
- The application services module is free of Spring dependencies, easing reuse
  in alternative hosting environments or worker processes.
