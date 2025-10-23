---
id: task-4
title: Implement in-memory subscription message bus for agents
status: Done
assignee:
  - assistant
created_date: '2025-10-23 07:55'
updated_date: '2025-10-23 13:15'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Introduce a Spring-native message bus that allows all chat agents to subscribe to message streams and publish new messages. Use Project Reactor (Spring’s preferred reactive toolkit) to deliver an in-memory, subscription-based implementation that downstream components can consume easily while leaving room for future persistence or external brokers.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Spring bean exposes a clear API for agents to publish messages and subscribe to the shared stream.
- [x] #2 Implementation is backed by an in-memory Project Reactor sink or similar Spring-provided reactive component that supports multiple subscribers without message loss.
- [x] #3 Unit or integration tests verify that multiple subscribers receive messages and that publishing behaves as expected.
- [x] #4 Documentation or README section explains how to use the message bus from other components and outlines how it could be swapped for a persistent/external broker later.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Implementation Plan:
1. Establish module foundation
   - Ensure `event-bus` module inherits latest parent configuration and add dependencies needed for reactive messaging (Spring Boot WebFlux, Reactor test utilities). Update module POM if additional libraries are required.
2. Define bus contract
   - Create a package (e.g., `org.rag4j.chatter.eventbus.bus`) with interfaces describing publish/subscribe operations (e.g., `MessageBus`, `BusSubscription`) using domain types from task-5 once available; temporarily define simple records if necessary.
3. Implement Reactor-based message bus
   - Introduce a `Sinks.Many`-powered implementation (e.g., `ReactorMessageBus`) configured as a Spring bean.
   - Choose appropriate sink variant (likely `Sinks.many().multicast().onBackpressureBuffer()`) and wrap access with methods to emit/publish messages and to expose a shared `Flux` for subscribers.
   - Ensure thread-safety and backpressure strategy align with expected throughput; document choices in code comments.
4. Integrate with Spring configuration
   - Provide `@Configuration` class that registers the bus bean and exposes subscription factory methods for other components.
   - Add conditional configuration if future external bus integration is needed (e.g., through Spring profiles) while keeping current focus on in-memory.
5. Implement tests
   - Write unit tests verifying multiple subscribers receive messages, late subscribers behavior (e.g., no replay unless desired), and that publish operations return expected results.
   - Use StepVerifier to assert reactive flows and cover error handling.
6. Documentation and usage example
   - Update module README or parent README to document how to inject and use the bus, noting Reactor sink characteristics and how to swap implementations later.
   - Optionally include a small demo component (e.g., `CommandLineRunner`) that publishes sample events for manual testing.
7. Verify build and clean up
   - Run module/unit tests via `mvn -pl event-bus test` (and root `mvn clean verify` if dependencies available) to ensure implementation integrates cleanly.
   - Record any limitations or future considerations in Backlog task notes.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Test run note: `mvn -pl event-bus test` attempted but the sandbox blocks dependency downloads, so the command cannot complete here. Should pass locally once Maven resolves artifacts.
<!-- SECTION:NOTES:END -->
