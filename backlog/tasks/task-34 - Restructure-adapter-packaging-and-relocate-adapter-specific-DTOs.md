---
id: task-34
title: Restructure adapter packaging and relocate adapter-specific DTOs
status: To Do
assignee: []
created_date: '2025-10-25 21:28'
labels:
  - architecture
  - infrastructure
  - hexagonal
dependencies:
  - task-31
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Introduce a clearer adapter structure (e.g., web-adapter, websocket-adapter, sse-adapter packages or modules) within the infrastructure layer to separate REST/WebSocket concerns from application services. Move adapter-only DTOs like MessageDto into the corresponding adapter package/module. Update imports accordingly.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Infrastructure packages/modules organised by adapter type, with controllers, DTOs, and configuration grouped appropriately.
- [ ] #2 MessageDto and similar transport-specific types live alongside their adapters instead of mixed into application coordination packages.
- [ ] #3 Application modules no longer depend on adapter DTOs.
- [ ] #4 Update documentation to reflect the new structure and ensure build/tests remain green.
<!-- AC:END -->
