---
id: task-4
title: Implement in-memory subscription message bus for agents
status: To Do
assignee: []
created_date: '2025-10-23 07:55'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Introduce a Spring-native message bus that allows all chat agents to subscribe to message streams and publish new messages. Use Project Reactor (Spring’s preferred reactive toolkit) to deliver an in-memory, subscription-based implementation that downstream components can consume easily while leaving room for future persistence or external brokers.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Spring bean exposes a clear API for agents to publish messages and subscribe to the shared stream.
- [ ] #2 Implementation is backed by an in-memory Project Reactor sink or similar Spring-provided reactive component that supports multiple subscribers without message loss.
- [ ] #3 Unit or integration tests verify that multiple subscribers receive messages and that publishing behaves as expected.
- [ ] #4 Documentation or README section explains how to use the message bus from other components and outlines how it could be swapped for a persistent/external broker later.
<!-- AC:END -->
