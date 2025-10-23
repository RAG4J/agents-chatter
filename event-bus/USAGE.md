# Using event-bus as a Library

The `event-bus` module is designed to be used as a library dependency in other Spring Boot applications. It provides a reactive message bus for broadcasting messages to multiple subscribers.

## Adding the Dependency

In your module's `pom.xml`:

```xml
<dependency>
    <groupId>org.rag4j.chatter</groupId>
    <artifactId>event-bus</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

## What You Get

The event-bus library provides:

- **`MessageBus` interface** - Contract for publishing and subscribing to messages
- **`ReactorMessageBus` implementation** - Reactor-based hot stream message bus
- **`MessageEnvelope`** - Message wrapper with id, author, payload, and timestamp
- **Auto-configuration** - `MessageBus` bean is automatically available for injection

## Basic Usage

### 1. Inject MessageBus

```java
import org.rag4j.chatter.eventbus.bus.MessageBus;
import org.rag4j.chatter.eventbus.bus.MessageEnvelope;
import org.springframework.stereotype.Component;

@Component
public class ChatService {
    
    private final MessageBus messageBus;
    
    public ChatService(MessageBus messageBus) {
        this.messageBus = messageBus;
    }
}
```

### 2. Publish Messages

```java
public void sendMessage(String author, String content) {
    var message = MessageEnvelope.from(author, content);
    boolean published = messageBus.publish(message);
    
    if (!published) {
        // No subscribers are listening
        log.warn("Message not delivered - no active subscribers");
    }
}
```

### 3. Subscribe to Messages

```java
@PostConstruct
public void subscribeToMessages() {
    messageBus.stream()
        .subscribe(
            message -> handleMessage(message),
            error -> log.error("Stream error", error),
            () -> log.info("Stream completed")
        );
}

private void handleMessage(MessageEnvelope message) {
    log.info("Received: {} from {}", message.payload(), message.author());
}
```

## Advanced Usage

### Filtering Messages

```java
messageBus.stream()
    .filter(msg -> msg.author().equals("alice"))
    .subscribe(msg -> processAliceMessages(msg));
```

### Transforming Messages

```java
messageBus.stream()
    .map(msg -> msg.payload().toUpperCase())
    .subscribe(uppercaseContent -> processContent(uppercaseContent));
```

### Multiple Subscribers

Each subscriber receives all messages published after they subscribe (hot stream):

```java
// Subscriber 1
messageBus.stream().subscribe(msg -> log.info("Sub 1: {}", msg.payload()));

// Subscriber 2
messageBus.stream().subscribe(msg -> log.info("Sub 2: {}", msg.payload()));

// Both subscribers receive this message
messageBus.publish(MessageEnvelope.from("system", "Hello all"));
```

### Server-Sent Events (SSE)

Expose messages to web clients via SSE:

```java
@GetMapping(value = "/messages/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<ServerSentEvent<MessageDto>> streamMessages() {
    return messageBus.stream()
        .map(envelope -> ServerSentEvent.<MessageDto>builder()
            .id(envelope.id().toString())
            .event("message")
            .data(toDto(envelope))
            .build());
}
```

### Backpressure Handling

Handle slow consumers gracefully:

```java
messageBus.stream()
    .onBackpressureDrop(dropped -> log.warn("Dropped message: {}", dropped.id()))
    .subscribe(msg -> slowProcessor(msg));
```

## Key Behaviors

### Hot Stream
- Subscribers only receive messages published **after** they subscribe
- Historical messages are **not** replayed
- All active subscribers receive the same messages simultaneously

### Publish Returns Boolean
- Returns `true` if message was successfully delivered to all subscribers
- Returns `false` if:
  - No subscribers are connected
  - Subscriber(s) can't keep up with backpressure
  - The sink has been terminated

### Thread Safety
The `MessageBus` is thread-safe and can be used from multiple threads concurrently.

## Example: REST Controller

See `web-app/src/main/java/org/rag4j/chatter/web/api/MessageController.java` for a complete example of:
- Publishing messages via POST endpoint
- Streaming messages via SSE GET endpoint

## Testing

The event-bus module includes comprehensive tests demonstrating usage patterns. See:
- `ReactorMessageBusTests.java` - Unit tests for core functionality

## Further Reading

- [reactor.md](reactor.md) - Detailed explanation of the Reactor implementation
- [Project Reactor Documentation](https://projectreactor.io/docs/core/release/reference/)
