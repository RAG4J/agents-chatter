---
id: task-12
title: Prevent agents from posting empty "nothing to say" messages
status: To Do
assignee: []
created_date: '2025-10-24 05:04'
updated_date: '2025-10-24 05:15'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Agents currently reply with literal conversational text when they have nothing to share, which still appears in the thread. Introduce a dedicated placeholder token (e.g. `#nothingtosay#`) that the runtime interprets as "no message" and suppresses from the output so the agent skips posting altogether. Ensure other participants understand that the agent simply had nothing relevant without seeing a placeholder message.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Agent run produces no visible post when the model emits the `#nothingtosay#` placeholder.
- [ ] #2 Prompting/messaging logic updated so agents emit `#nothingtosay#` for empty turns and the runtime suppresses it.
- [ ] #3 Manual test or scenario documented showing an agent emitting `#nothingtosay#` and the system skipping the post without errors.
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Tested via `SubscriberAgentPlaceholderTests` which sends a user message, forces the agent to emit `#nothingtosay#`, and verifies no message is published back.
<!-- SECTION:NOTES:END -->
