# Agents Chatter

Baseline multi-module workspace for agent-chat experiments. The backend stack currently comprises Spring Boot 3.5 and Java 21; additional modules will host the message bus and frontend integrations.

## Project Layout

- `pom.xml` — parent Maven project aggregating shared dependency management and plugins.
- `web-app` — Spring Boot WebFlux service exposing REST/WebSocket chat endpoints.
- `event-bus` — Spring Boot module exposing an in-memory Reactor-powered message bus for chat agents.
- `frontend` — Next.js + Chakra UI chat client scaffolded for React consumption.
- `backlog/` — task tracking via Backlog.md (do not edit manually).

Future work will introduce a shared `chat-domain` module (see task-5).

## Prerequisites

- Java 21 compatible toolchain. Maven targets Java 21 using the compiler `--release` flag; configure `JAVA_HOME` to point to a JDK ≥21 (JDK 25 works as well).
- Maven 3.9+. The project includes plugin management, so invoking the parent build is recommended: `mvn clean verify`.

## Building

From the project root:

```bash
mvn clean verify
```

The command compiles both modules and runs their unit tests. Maven will download Spring Boot dependencies on the first run.

## Running the Web App

Launch the sample WebFlux service:

```bash
mvn -pl web-app spring-boot:run
```

The application exposes a status endpoint returning service metadata:

```
GET http://localhost:8080/api/status
```

### Chat API

All chat endpoints live under `/api/messages` and speak JSON friendly to React clients:

| Method | Path | Description |
| --- | --- | --- |
| `GET` | `/api/messages` | Returns the in-memory history of messages (id, author, payload, timestamp). |
| `POST` | `/api/messages` | Publishes a new message and echoes the created payload. Body shape: `{ "author": "alice", "payload": "Hello" }`. |

### WebSocket Endpoint

Realtime updates and bidirectional messaging are handled through a WebSocket channel:

- URL: `ws://localhost:8080/ws/messages`
- Outgoing messages are JSON encoded `MessageDto` objects (same shape as the REST response).
- Clients send JSON payloads matching the POST body (`author`, `payload`) to broadcast to all subscribers.
- A built-in `Echo Agent` subscribes to the message bus and replies with an `echo …` message for every received message (except its own); disable or extend it in `web-app/src/main/java/org/rag4j/chatter/web/agents`.

### Presence API

Track who is currently connected:

- `GET /api/presence` returns the participant list (agents + humans) with their online status and connection counts.
- `GET /api/presence/stream` (text/event-stream) emits incremental updates for realtime dashboards.
- WebSocket clients can set a `participant` query parameter (e.g., `ws://…/ws/messages?participant=You`) to register their display name; otherwise "You" is assumed.

Basic browser example:

```javascript
const socket = new WebSocket("ws://localhost:8080/ws/messages");
socket.onmessage = event => console.log("Incoming", JSON.parse(event.data));
socket.onopen = () => socket.send(JSON.stringify({ author: "react-client", payload: "Hello!" }));
```

### Environment Variables

None required for the chat API; everything runs in-memory. Future tasks will introduce provider credentials or persistence configuration.

## Frontend (Next.js)

The `frontend` module houses the React/Next.js experience. To work directly with npm:

```bash
cd frontend
npm install
npm run dev
```

Available scripts:

- `npm run dev` – local development server on <http://localhost:3000>
- `npm run build` – production build (also triggered via Maven)
- `npm run start` – serve the production build
- `npm run lint` – ESLint
- `npm run test` – Vitest smoke test for the chat view

Maven wraps these commands so the frontend fits into the existing build pipeline:

```bash
mvn -pl frontend verify
```

Environment variables for frontend integration live in `frontend/.env.example` (`NEXT_PUBLIC_API_BASE`, `NEXT_PUBLIC_WS_URL`).

## Event Bus Module

The `event-bus` module provides the reusable `MessageBus` abstraction used by the web application. It relies on Reactor `Sinks.many().multicast().onBackpressureBuffer()` to broadcast messages to an arbitrary number of subscribers and is ready to be swapped with an external broker-backed implementation when needed.

Key types:

- `MessageBus` — publish/stream contract.
- `ReactorMessageBus` — in-memory implementation registered as a Spring bean.
- `MessageEnvelope` — lightweight message descriptor shared between modules.

## Tooling Notes

- The Maven parent centralises the Spring Boot BOM, so child modules only declare starter dependencies.
- The compiler plugin is configured at the root to ensure all modules build with Java 21 bytecode (even when the host JVM is newer).
- For IDE import, open the root `pom.xml` as a Maven project to discover both modules automatically.

## Next Steps

- Enhance message persistence (database or event store) and delivery guarantees.
- Explore authentication/authorization for agent and human participants.
