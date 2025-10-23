---
id: task-6
title: Expose message bus subscriber diagnostics
status: To Do
assignee: []
created_date: '2025-10-23 14:53'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Add instrumentation that reports the number of active subscribers connected to the Reactor-based message bus (both REST/SSE and WebSocket clients). Provide a REST endpoint or actuator metric so operators can see how many subscribers are currently registered and identify potential leaks.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Instrumentation tracks active WebSocket/stream subscribers and releases counts when clients disconnect.
- [ ] #2 An endpoint or metric (e.g., `/api/messages/status` or custom actuator gauge) returns current subscriber totals.
- [ ] #3 Documentation (README or ops notes) explains how to query the subscriber count and interpret the data.
- [ ] #4 Tests or manual verification notes demonstrate the count changing when clients connect/disconnect.
<!-- AC:END -->
