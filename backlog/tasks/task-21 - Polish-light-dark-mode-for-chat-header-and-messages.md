---
id: task-21
title: Polish light/dark mode for chat header and messages
status: To Do
assignee: []
created_date: '2025-10-25 08:26'
updated_date: '2025-10-26 08:23'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Improve theme support by addressing the readability issues in the top navigation/header and message bubbles.

Scope:
- Audit Chakra UI color usage for the chat shell header, message list/bubbles, and surrounding panels in both light and dark modes.
- Ensure titles and key labels in the top bar adapt to the active color mode and meet basic contrast requirements.
- Refresh light-mode backgrounds/foregrounds for the chat shell, message container, and conversation insights so the experience feels intentional while preserving dark-mode styling.

Out of scope: broader UI layout changes, presence panel redesign, or global theme overhaul.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 #1 Chat header (titles, status, participant indicators) achieves readable contrast and cohesive styling in light mode while preserving dark mode appearance.
- [ ] #2 #2 Chat shell, message container, and conversation insights surfaces all adopt mode-appropriate backgrounds/accents with no regressions in dark mode.
- [ ] #3 #3 Agent and human message bubbles retain legibility and visual hierarchy after the light-mode refresh.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Audit the entire chat experience under both color modes, noting gaps in background updates for the page shell, message column, and conversation insights panel in addition to header/message text.
2. Define a cohesive set of light/dark tokens (e.g., surface, panel, accent) and Chakra mode-aware styles that cover the page background, chat container, message list wrapper, and insights card.
3. Apply the updated tokens to `ChatShell`, header, message container, and conversation insights components so each surface adapts correctly per mode while keeping existing layout/typography.
4. Revisit message bubble colors to ensure they complement the new surface treatments and still deliver sufficient contrast for agent/human roles.
5. Manual QA across modes and common breakpoints, logging any regressions, then run lint/tests to confirm no unintended styling changes elsewhere.
<!-- SECTION:PLAN:END -->
