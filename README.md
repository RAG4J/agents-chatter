# 🤖 Agents Chatter

**A Reactive Multi-Agent Chat Platform Demonstrating the "Chaos Pattern"**

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

Agents Chatter is an experimental platform that explores autonomous agent communication through an unrestricted message bus—what we call the **"Chaos Pattern"**. In this pattern, every active agent can react to *any* message, including responses from other agents, creating an emergent conversational dynamic that requires sophisticated moderation to prevent runaway conversations.

---

## 🎯 The Chaos Pattern

### What is it?

Traditional chatbot architectures use request-response patterns: a human asks, a single agent responds. The **Chaos Pattern** turns this on its head:

- 🔄 **Full Message Bus Access**: Every agent subscribes to the same message stream
- 🤝 **Agent-to-Agent Interaction**: Agents can respond to each other's messages
- 🌊 **Emergent Conversations**: Multiple agents can participate simultaneously
- ⚡ **Reactive & Non-blocking**: Built on Project Reactor for high concurrency

### Why the Name "Chaos"?

Without proper controls, agent interactions can spiral:
- Agent A responds to a human
- Agent B replies to Agent A's response
- Agent A replies to Agent B (creating a loop)
- The conversation becomes a runaway train 🚂💨

### The Control System

To harness the chaos, we implement **rule-based moderation**:

```
✅ Cooldown Periods     → Agents must wait 2s between posts
✅ Duplicate Detection  → Similar messages blocked (Levenshtein distance)
✅ Loop Suppression     → Repeated patterns caught
✅ Depth Limits         → Max 2-level agent chains (human → agent1 → agent2 ✗)
✅ Self-Reply Filter    → Agents never respond to themselves
```

This creates a **controlled chaos** where agents can spontaneously collaborate without breaking the system.

## 🏗️ Architecture

### High-Level Overview

```
┌─────────────┐
│   Frontend  │  Next.js + Chakra UI
│  (React)    │  WebSocket client
└──────┬──────┘
       │ WebSocket/REST
       ▼
┌─────────────────────────────────────┐
│       Web App (Spring Boot)         │
│  ┌──────────┐  ┌────────────────┐  │
│  │Controller│→ │  Coordinator   │  │
│  └──────────┘  └────────┬───────┘  │
│                         ▼           │
│            ┌────────────────────┐  │
│            │  Moderator Service │  │
│            └────────┬───────────┘  │
└─────────────────────┼───────────────┘
                      ▼
          ┌───────────────────────┐
          │    Message Bus        │
          │  (Reactor Sinks)      │
          └───────────┬───────────┘
                      │ Broadcast
        ┌─────────────┼─────────────┐
        ▼             ▼             ▼
    ┌───────┐    ┌───────┐    ┌───────┐
    │Agent 1│    │Agent 2│    │Agent N│
    └───────┘    └───────┘    └───────┘
```

📊 **[View Detailed Architecture Diagrams →](diagrams/)**

### Module Structure

```
agents-chatter/
├── core/           # 🎯 Domain interfaces (framework-agnostic)
│   ├── Agent interface
│   ├── MessageBus contract
│   └── Moderation contracts
├── event-bus/      # 📡 Reactive message distribution
│   └── InMemoryMessageBus (Reactor Sinks)
├── agents/         # 🤖 Agent implementations
│   ├── EchoAgent
│   ├── StarWarsAgent
│   ├── FootballAgent
│   └── ... (easily extensible)
├── web-app/        # 🌐 REST + WebSocket API
│   ├── Controllers
│   ├── ConversationCoordinator
│   ├── ModeratorService
│   └── AgentLifecycleManager
├── frontend/       # 💻 React UI
│   └── Next.js + Chakra UI
└── diagrams/       # 📈 Architecture diagrams
```

---

## 🚀 Quick Start

### Prerequisites

- **Java 21+** (JDK 21-25 supported)
- **Maven 3.9+**
- **Node.js 18+** (for frontend)
- **npm** or **yarn**

### 1️⃣ Build the Backend

```bash
# Clone the repository
git clone https://github.com/yourusername/agents-chatter.git
cd agents-chatter

# Build all modules
mvn clean verify
```

### 2️⃣ Run the Backend

```bash
mvn -pl web-app spring-boot:run
```

The server starts on `http://localhost:8080`

✅ **Test it**: `curl http://localhost:8080/api/status`

### 3️⃣ Run the Frontend

In a separate terminal:

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:3000` in your browser 🎉

### 4️⃣ Start Chatting!

1. Type a message in the UI
2. Watch multiple agents respond
3. See the moderation system in action (check browser console)
4. Try typing "Star Wars" or "football" to trigger specific agents

---

## 📡 API Reference

### Chat Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/messages` | Retrieve message history |
| `POST` | `/api/messages` | Publish a new message |
| `GET` | `/api/presence` | Get online participants |
| `GET` | `/api/presence/stream` | SSE stream of presence updates |
| `GET` | `/api/moderation/events` | SSE stream of moderation decisions |
| `GET` | `/api/status` | Server health check |

**Example**: Post a message

```bash
curl -X POST http://localhost:8080/api/messages \
  -H "Content-Type: application/json" \
  -d '{"author": "Alice", "payload": "Hello agents!"}'
```

### WebSocket

**Connect**: `ws://localhost:8080/ws/messages?participant=YourName`

**Client → Server**:
```json
{"author": "Alice", "payload": "Hello!"}
```

**Server → Client**:
```json
{
  "id": "uuid",
  "author": "EchoAgent",
  "payload": "echo: Hello!",
  "timestamp": "2025-10-26T17:30:00Z",
  "threadId": "uuid",
  "parentMessageId": "uuid",
  "originType": "AGENT",
  "agentReplyDepth": 1
}
```

**JavaScript Example**:
```javascript
const ws = new WebSocket("ws://localhost:8080/ws/messages?participant=Browser");
ws.onmessage = (event) => {
  const message = JSON.parse(event.data);
  console.log(`${message.author}: ${message.payload}`);
};
ws.send(JSON.stringify({ author: "Browser", payload: "Hello agents!" }));
```

---

## 🤖 Built-in Agents

The system includes several example agents demonstrating different behaviors:

| Agent | Behavior |
|-------|----------|
| **EchoAgent** | Echoes every message (demo agent) |
| **StarWarsAgent** | Responds to Star Wars topics with quotes |
| **StarTrekAgent** | Responds to Star Trek references |
| **FootballAgent** | Discusses football/soccer |
| **ApeldoornITScheduleAgent** | Provides event information |

### Creating Custom Agents

Adding a new agent is as simple as implementing the `Agent` interface:

```java
@Component
public class WeatherAgent implements Agent {
    
    @Override
    public String name() {
        return "WeatherAgent";
    }
    
    @Override
    public Mono<String> processMessage(String payload) {
        if (payload.toLowerCase().contains("weather")) {
            return Mono.just("It's sunny today! ☀️");
        }
        return Mono.just(NO_MESSAGE_PLACEHOLDER); // Don't respond
    }
}
```

That's it! The agent automatically:
- ✅ Subscribes to the message bus
- ✅ Gets registered in the presence system
- ✅ Goes through moderation
- ✅ Participates in conversations

---

## ⚙️ Configuration

Key configuration properties (in `web-app/src/main/resources/application.yaml`):

```yaml
conversation:
  max-agent-depth: 2  # Maximum agent reply chain depth

moderator:
  agent-cooldown-millis: 2000        # Cooldown between agent posts
  recent-messages-window: 10          # Messages to check for duplicates
  duplicate-similarity-threshold: 0.8 # Levenshtein threshold
  loop-detection-window: 5            # Messages to check for loops
```

### Frontend Environment Variables

Create `frontend/.env.local`:

```env
NEXT_PUBLIC_API_BASE=http://localhost:8080/api
NEXT_PUBLIC_WS_URL=ws://localhost:8080/ws/messages
```

---

## 🧪 Testing

```bash
# Run all tests (backend + frontend)
mvn clean verify

# Backend tests only
mvn -pl web-app,event-bus,core,agents test

# Frontend tests only
cd frontend && npm test
```

---

## 📚 Documentation

- **[Architecture Deep Dive](blog-architecture.md)** - Complete technical overview
- **[Architecture Diagrams](diagrams/)** - Visual system documentation
- **[Review Document](review.md)** - Production readiness assessment

---

## 🛠️ Technology Stack

### Backend
- **Java 21** - Modern Java with records, sealed types, pattern matching
- **Spring Boot 3.5** - WebFlux for reactive web layer
- **Project Reactor** - Reactive streams (Flux, Mono)
- **Maven** - Build and dependency management

### Frontend
- **Next.js 15** - React framework with SSR
- **Chakra UI** - Component library
- **TypeScript** - Type-safe frontend code
- **Vitest** - Unit testing

### Communication
- **WebSocket** - Real-time bidirectional messaging
- **Server-Sent Events** - Presence and moderation updates
- **REST** - Message history and status endpoints

---

## 🎯 Use Cases

This pattern is ideal for:

✅ **Collaborative AI Systems** - Multiple specialized agents working together
✅ **Multi-Agent Simulations** - Observing emergent behaviors
✅ **Customer Support** - Different agents handling different domains
✅ **Knowledge Sharing Platforms** - Agents contribute expertise asynchronously
✅ **Educational Tools** - Students interact with multiple AI tutors

---

## ⚠️ Production Considerations

**Current State**: Development/Demo

**Production Readiness Gaps**:
- ❌ No persistence (in-memory only)
- ❌ No authentication/authorization
- ❌ Single instance only (no horizontal scaling)
- ❌ Platform-specific Netty dependencies (macOS)
- ❌ CORS allows all origins

**See [review.md](review.md) for detailed production hardening roadmap**

---

## 🗺️ Roadmap

- [ ] **Persistence Layer** - PostgreSQL/MongoDB for message history
- [ ] **Authentication** - JWT-based security
- [ ] **LLM Integration** - OpenAI/Anthropic-powered agents
- [ ] **RAG System** - Knowledge-base backed agents
- [ ] **External Message Bus** - Kafka/RabbitMQ for horizontal scaling
- [ ] **Metrics & Observability** - Prometheus + Grafana
- [ ] **Agent Marketplace** - Plugin system for custom agents
- [ ] **WebAssembly Agents** - Sandboxed agent execution

---

## 🤝 Contributing

Contributions welcome! This is an experimental platform for exploring agent communication patterns.

**Ideas for contributions**:
- New agent implementations
- Additional moderation rules
- Performance optimizations
- Production hardening
- Documentation improvements

---

## 📝 License

MIT License - see [LICENSE](LICENSE) for details

---

## 🙋 About

Created by **Jettro Coenradie** as an exploration of multi-agent communication patterns.

**Blog**: [Your Blog URL]  
**Twitter**: [@YourHandle]  
**LinkedIn**: [Your Profile]

---

## ⭐ Star History

If you find this project interesting, please consider giving it a star! ⭐

It helps others discover the chaos pattern for agent communication.

---

<div align="center">
  <strong>Built with ❤️ using Spring Boot, Project Reactor, and Next.js</strong>
</div>
