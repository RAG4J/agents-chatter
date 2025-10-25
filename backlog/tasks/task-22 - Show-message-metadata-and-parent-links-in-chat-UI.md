---
id: task-22
title: Show message metadata and parent links in chat UI
status: To Do
assignee: []
created_date: '2025-10-25 08:34'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Improve message discoverability by surfacing metadata and hierarchy in the chat interface.

Scope:
- Add a small icon (e.g., info/metadata glyph) to each chat message bubble. Hovering/focusing the icon should reveal a tooltip or popover displaying key envelope data (id, thread id, parent id, timestamps, origin, depth).
- Highlight parent relationships: when a user hovers a message, visually emphasize its parent message (e.g., subtle background glow). Ensure this works for nested threads without overwhelming the UI.
- Ensure interactions are accessible (keyboard + screen reader friendly) and behave appropriately in both light/dark mode.

Out of scope: broader redesign of message layout, reply threading controls, or persistence changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Hovering or focusing the message icon displays envelope metadata for that message.
- [ ] #2 Hovering/focusing a message visually distinguishes its parent message; behaviour is testable for at least two nested replies.
- [ ] #3 Accessibility considerations are addressed (keyboard focus, aria labels) and styling works in both themes.
<!-- AC:END -->
