# Agents Chatter Frontend

Next.js 15 + Chakra UI chat experience for interacting with human teammates and AI agents.

## Prerequisites

- Node.js 20 (managed automatically when you run through Maven; otherwise install manually).
- npm 10+ (comes with Node 20).

## Getting Started

```bash
npm install
npm run dev
```

Visit <http://localhost:3000> to open the chat interface. The app loads mock messages by default and is ready to integrate with the backend REST/WebSocket endpoints exposed at `http://localhost:8080/api/messages` and `ws://localhost:8080/ws/messages`.

## Environment Variables

Copy `.env.example` to `.env.local` and adjust as needed:

```bash
NEXT_PUBLIC_API_BASE=http://localhost:8080/api
NEXT_PUBLIC_WS_URL=ws://localhost:8080/ws/messages
```

If these variables are omitted, the UI falls back to mock data only.

## Available Scripts

- `npm run dev` – start the Next.js development server with fast refresh.
- `npm run build` – create an optimized production build.
- `npm run start` – serve the production build.
- `npm run lint` – run ESLint over the project.
- `npm run test` – execute Vitest unit tests (currently a lightweight smoke test for the chat view).

## Maven Integration

From the repository root you can execute:

```bash
mvn -pl frontend verify
```

The `frontend-maven-plugin` will install the required Node/npm versions, run `npm install`, and execute `npm run build`. This keeps the frontend pipeline aligned with the backend’s Maven workflow.

## Project Structure

- `app/` – Next.js App Router entry points.
- `components/` – Chat UI components (header, message list, composer, etc.).
- `hooks/` – Client-side hooks for realtime messaging.
- `lib/` – Shared utilities, theme configuration, and mock data.
- `tests/` – Vitest test suites.

## Next Steps

- Add authentication/agent presence indicators.
- Expand the right-hand insights panel with live diagnostics or summaries.
- Implement richer error handling (retry queues, offline support).
