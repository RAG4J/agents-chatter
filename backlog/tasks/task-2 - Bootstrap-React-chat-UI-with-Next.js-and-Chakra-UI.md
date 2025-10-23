---
id: task-2
title: Bootstrap React chat UI with Next.js and Chakra UI
status: Done
assignee:
  - assistant
created_date: '2025-10-23 07:35'
updated_date: '2025-10-23 14:33'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Create a new frontend project using React powered by Next.js and styled with Chakra UI to deliver a professional, clean chat interface. Scaffold the application, configure Chakra UI theming, and implement an initial chat layout that can display messages from both connected agents and human users, ready for future data integration.

Use maven as a wrapper for the built. If Next.js and Chakra require other tools, try to wrap them or call them from Maven.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Next.js project is initialized with TypeScript support and Chakra UI installed and configured with a base theme.
- [x] #2 Chat view renders distinct styling for agent vs. human messages using mock data and handles responsive layout.
- [ ] #3 Project scripts run successfully (`npm install`, `npm run dev`, `npm run build`) without errors.
- [x] #4 README documents project setup, available scripts, and how to adjust theming or message mocks.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Implementation Plan:
1. Confirm tech stack and project layout decisions
   - Verify Next.js 15 (or latest stable) with TypeScript fits deployment requirements, and that Chakra UI is the chosen component library.
   - Decide on package manager (pnpm vs npm) and how Maven will invoke frontend scripts (via npm wrapper or Maven frontend plugin).
2. Scaffold Next.js project
   - Use `create-next-app` to initialize a TypeScript project under `frontend/` (or similar), selecting "App Router" and ensuring ESLint/Prettier are configured.
   - Configure project scripts (`dev`, `build`, `start`) and add additional scripts if needed for storybook or linting.
3. Integrate Chakra UI and global styling
   - Install Chakra UI, emotion dependencies, and set up a theme provider (`ChakraProvider`) in the Next.js root layout.
   - Define a base theme with brand colors consistent with the desired “professional clean” look; include dark/light mode toggle if desired.
4. Build chat interface components
   - Create reusable components for message list, message bubbles (agent vs human), composer input, and header.
   - Implement mock data provider to simulate messages from agents and humans; ensure stylistic differences (colors, alignment) are obvious yet accessible.
   - Ensure layout is responsive (mobile to desktop) using Chakra’s responsive styles or CSS grid.
5. Wire data fetching stubs
   - Abstract message fetching and posting through a service layer (e.g., `lib/api/messages.ts`) that currently returns mock data but is ready to call the REST/WebSocket APIs from task-3.
   - Set up environment configuration (NEXT_PUBLIC_API_BASE, NEXT_PUBLIC_WS_URL) with `.env.example` explaining expected values.
6. WebSocket integration placeholder
   - Stub out WebSocket client hook (`useMessagesFeed`) prepared to connect to `/ws/messages`; for now simulate streaming updates from mock data.
   - Document how to enable real WebSocket once backend endpoints are reachable.
7. Testing and linting setup
   - Ensure ESLint rules (Next.js default) pass; add basic unit test scaffolding with Jest/Testing Library or Playwright smoke test for the chat view.
8. Documentation updates
   - Update root README section for the frontend: installation instructions (`npm install`), `npm run dev`, `npm run build`, environment variable setup, and integration notes with backend endpoints.
   - Mention how Maven can invoke the frontend build if required.
9. Verification
   - Run `npm run lint`, `npm run test` (if applicable), `npm run build` to ensure the project is production-ready.
   - Capture any follow-up tasks (e.g., hooking up real APIs) in backlog notes.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Frontend stack: Next.js 15 (TypeScript, App Router), Chakra UI 2.x, npm as package manager. Maven integration via frontend-maven-plugin to wrap install/build.

Frontend build not executed locally in sandbox because npm install requires network access. Maven `frontend` module configured to run `npm install` and `npm run build`; run `mvn -pl frontend verify` or `npm install && npm run build` on a connected machine.
<!-- SECTION:NOTES:END -->
