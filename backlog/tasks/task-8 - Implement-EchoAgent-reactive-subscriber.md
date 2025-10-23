---
id: task-8
title: Implement EchoAgent reactive subscriber
status: Done
assignee:
  - assistant
created_date: '2025-10-23 17:26'
updated_date: '2025-10-23 17:32'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Enhance the web-app module so the existing EchoAgent registers as a subscriber on startup, listens to all incoming chat messages, and publishes an echo response (prefixed with "echo") to the message bus. Ensure EchoAgent ignores messages it produced itself to avoid loops.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 EchoAgent registers with the MessageBus on application startup and subscribes to incoming messages.
- [x] #2 For each message not authored by EchoAgent, publish a response message prefixed with "echo" (e.g., "echo Hello").
- [x] #3 EchoAgent ignores its own messages to prevent infinite echo loops.
- [x] #4 Add tests or manual verification steps demonstrating an echoed response when a human/other agent sends a message.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Implementation Plan:
1. Review existing EchoAgent and message bus integration points
   - Inspect the web-app module for an existing EchoAgent component or service and understand how MessageService/MessageBus interacts with chat messages.
   - Confirm message structure and identify how to tag messages authored by EchoAgent (e.g., author name).
2. Register EchoAgent subscriber
   - Create a Spring-managed bean that listens to the MessageBus stream (`messageBus.stream()`) on application startup.
   - Use Reactor operators to subscribe with appropriate scheduler/backpressure handling.
3. Implement echo logic
   - For each incoming MessageEnvelope, skip if the author matches EchoAgent’s identifier.
   - Construct a new MessageEnvelope with author “EchoAgent”, payload prefixed with “echo ”, and publish via the MessageService/MessageBus.
4. Avoid loops and duplicate processing
   - Ensure the echo publication does not re-trigger the agent (e.g., by relying on author filtering or message metadata).
   - Consider debouncing if multiple agents might generate cascaded echoes in future.
5. Testing & verification
   - Write a unit test using a stub MessageBus/MessageService verifying the agent publishes echoes for external messages but not its own.
   - Perform manual test via REST/WebSocket (using curl or the frontend) to confirm echoes appear exactly once.
6. Documentation/update notes
   - Update README or developer notes outlining the EchoAgent behaviour and how to disable or extend it if necessary.
   - Record any configuration flags or TODOs for future enhancements.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Automated tests (`mvn -pl web-app test`) not executed in sandbox because Maven cannot download dependencies without network access.
<!-- SECTION:NOTES:END -->
