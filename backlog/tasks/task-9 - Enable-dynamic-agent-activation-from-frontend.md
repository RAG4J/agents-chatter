---
id: task-9
title: Enable dynamic agent activation from frontend
status: Done
assignee: []
created_date: '2025-10-23 22:50'
updated_date: '2025-10-26 11:45'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Allow agents to be started or stopped dynamically without relying on Spring profiles. Expose backend endpoints or commands so the frontend can request agent activation, and update the UI to allow users to toggle agents on/off at runtime.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Backend provides an API to list available agents and control their activation state without restarting the application.
- [ ] #2 Activating an agent registers it with the message bus and presence service; deactivating unsubscribes it cleanly.
- [ ] #3 Frontend surface lets users toggle agents on/off, reflecting current status in the presence list/header.
- [ ] #4 Documentation covers the new endpoints, activation flows, and any constraints (e.g., agent resource usage).
<!-- AC:END -->
