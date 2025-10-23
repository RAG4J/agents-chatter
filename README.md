# Agents Chatter

Baseline multi-module workspace for agent-chat experiments. The backend stack currently comprises Spring Boot 3.5 and Java 21; additional modules will host the message bus and frontend integrations.

## Project Layout

- `pom.xml` — parent Maven project aggregating shared dependency management and plugins.
- `web-app` — Spring Boot WebFlux service exposing a simple status endpoint.
- `event-bus` — Spring Boot module prepared for the upcoming message bus and realtime delivery features.
- `backlog/` — task tracking via Backlog.md (do not edit manually).

Future work will introduce a shared `chat-domain` module (see task-5) and the React frontend (task-2).

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

### Environment Variables

None required for the status endpoint; it emits static payload details and a timestamp. Future features may introduce configuration via environment variables.

## Event Bus Module

The `event-bus` module currently boots with a minimal configuration so upcoming tasks can add WebSocket streaming and the in-memory subscription bus. For now it shares the parent configuration and confirms the build pipeline works across modules.

## Tooling Notes

- The Maven parent centralises the Spring Boot BOM, so child modules only declare starter dependencies.
- The compiler plugin is configured at the root to ensure all modules build with Java 21 bytecode (even when the host JVM is newer).
- For IDE import, open the root `pom.xml` as a Maven project to discover both modules automatically.

## Next Steps

- Implement the shared `chat-domain` module (task-5) for reusable message/event types.
- Flesh out the REST + WebSocket backend (task-3) and Reactor-based in-memory bus (task-4).
- Scaffold the React/Next.js frontend (task-2) and integrate it with the backend endpoints once available.
