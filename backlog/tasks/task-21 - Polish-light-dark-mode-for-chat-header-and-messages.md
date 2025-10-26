---
id: task-21
title: Polish light/dark mode for chat header and messages
status: To Do
assignee: []
created_date: '2025-10-25 08:26'
updated_date: '2025-10-26 08:21'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Improve theme support by addressing the readability issues in the top navigation/header and message bubbles.

Scope:
- Audit Chakra UI color usage for the chat shell header and message list/bubbles in both light and dark modes.
- Ensure titles and key labels in the top bar adapt to the active color mode and meet basic contrast requirements.
- Update chat message backgrounds/text colors to remain legible in both modes without redesigning layout or typography.

Out of scope: broader UI restyling, presence panel redesign, or global theme overhaul.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Top bar titles and status labels remain readable in both light and dark modes (verified manually).
- [ ] #2 Chat messages (agent + human) maintain adequate contrast in both themes without regression to spacing/layout.
- [ ] #3 No other components are unintentionally restyled; changes limited to header and message list.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Audit the entire chat experience under both color modes, noting gaps in background updates for the page shell, message column, and conversation insights panel in addition to header/message text.
2. Define a cohesive set of light/dark tokens (e.g., surface, panel, accent) and Chakra mode-aware styles that cover the page background, chat container, message list wrapper, and insights card.
3. Apply the updated tokens to `ChatShell`, header, message container, and conversation insights components so each surface adapts correctly per mode while keeping existing layout/typography.
4. Revisit message bubble colors to ensure they complement the new surface treatments and still deliver sufficient contrast for agent/human roles.
5. Manual QA across modes and common breakpoints, logging any regressions, then run lint/tests to confirm no unintended styling changes elsewhere.
<!-- SECTION:PLAN:END -->
