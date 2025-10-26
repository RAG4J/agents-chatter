---
id: task-21
title: Polish light/dark mode for chat header and messages
status: To Do
assignee: []
created_date: '2025-10-25 08:26'
updated_date: '2025-10-26 08:15'
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
1. Review current Chakra theme usage within the chat header and message components to catalogue light/dark mode issues and contrast gaps.
2. Prototype updated color tokens (background, foreground, accents) for header titles/status and agent/human bubbles that satisfy contrast guidelines in both modes.
3. Apply the revised tokens/styles to `MessageHeader`, `ChatShell`, and message bubble components, verifying no layout regressions.
4. Manually validate light/dark mode appearance across header and message list (desktop + mobile breakpoints); adjust as needed for contrast and readability.
5. Capture before/after notes or screenshots and run lint/tests to ensure no unintended component styling changes elsewhere.
<!-- SECTION:PLAN:END -->
