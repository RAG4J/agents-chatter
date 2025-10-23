# Reactor in the Event-Bus Module

This guide explains how Project Reactor is used in the event-bus module to implement a reactive message bus for real-time chat messages.

## What is Project Reactor?

Project Reactor is a reactive programming library for building non-blocking, asynchronous applications on the JVM. It implements the Reactive Streams specification and provides two main types:

- **Mono**: A stream that emits 0 or 1 element
- **Flux**: A stream that emits 0 to N elements

Think of reactive streams as pipelines where data flows asynchronously. Instead of pulling data (imperative), subscribers receive data as it becomes available (reactive).

## Core Concepts in Our Implementation

### 1. Hot vs Cold Streams

**Cold streams** start emitting from the beginning for each new subscriber (like a movie on-demand).

**Hot streams** emit events in real-time, and subscribers only receive events after they subscribe (like a live TV broadcast).

Our message bus uses a **hot stream** because:
- New chat participants should only see new messages
- Historical messages aren't replayed
- All subscribers receive the same messages simultaneously

### 2. Sinks

A `Sink` is Reactor's way to programmatically push data into a reactive stream. It's the bridge between imperative code (calling `publish()`) and reactive streams.

```java
private final Sinks.Many<MessageEnvelope> sink;
```

The `Sinks.Many` allows multiple values to be emitted to multiple subscribers.

## The ReactorMessageBus Implementation

### Constructor

```java path=/Users/jettrocoenradie/Development/personal/agents-chatter/event-bus/src/main/java/org/rag4j/chatter/eventbus/bus/ReactorMessageBus.java start=22
    public ReactorMessageBus() {
        this(Sinks.many().multicast().directAllOrNothing());
    }
```

**Breaking it down:**
- `Sinks.many()` - Creates a sink that can emit multiple values
- `.multicast()` - All subscribers receive all messages (broadcast behavior)
- `.directAllOrNothing()` - Messages are pushed directly without buffering
  - Succeeds only if **all** active subscribers can receive the message
  - Fails if there are **no** subscribers
  - This ensures backpressure is respected and messages aren't lost

**Why `directAllOrNothing()` vs `onBackpressureBuffer()`?**

| Strategy | Behavior with No Subscribers | Buffering |
|----------|------------------------------|-----------|
| `directAllOrNothing()` | Fails (returns false) | No buffer |
| `onBackpressureBuffer()` | Succeeds (buffers message) | Unlimited buffer (memory leak risk) |

For a chat system, we want to know if messages can't be delivered immediately, hence `directAllOrNothing()`.

### Publishing Messages

```java path=/Users/jettrocoenradie/Development/personal/agents-chatter/event-bus/src/main/java/org/rag4j/chatter/eventbus/bus/ReactorMessageBus.java start=31
    @Override
    public boolean publish(MessageEnvelope message) {
        Assert.notNull(message, "message must not be null");
        var result = sink.tryEmitNext(message);
        if (result.isFailure()) {
            logger.debug("Failed to emit message {} due to {}", message.id(), result);
        }
        return result.isSuccess();
    }
```

**Key points:**
- `tryEmitNext()` - Non-blocking attempt to emit a value
- Returns an `EmitResult` indicating success or failure reason
- Failure can occur when:
  - No subscribers are listening
  - Subscribers can't keep up (backpressure)
  - The sink has been terminated

This is **imperative → reactive** bridging: synchronous method call → asynchronous stream emission.

### Streaming Messages

```java path=/Users/jettrocoenradie/Development/personal/agents-chatter/event-bus/src/main/java/org/rag4j/chatter/eventbus/bus/ReactorMessageBus.java start=41
    @Override
    public Flux<MessageEnvelope> stream() {
        return sharedFlux;
    }
```

**The shared Flux:**
```java path=/Users/jettrocoenradie/Development/personal/agents-chatter/event-bus/src/main/java/org/rag4j/chatter/eventbus/bus/ReactorMessageBus.java start=28
        this.sharedFlux = this.sink.asFlux();
```

- `sink.asFlux()` converts the sink into a `Flux` that subscribers can consume
- The Flux is stored as `sharedFlux` so all subscribers share the same stream
- Each call to `stream()` returns the same hot Flux reference

## How Subscribers Use the Stream

From the tests, here's a typical subscription pattern:

```java path=null start=null
var collector = bus.stream()
    .take(2)              // Take only 2 messages
    .collectList();        // Collect them into a List

StepVerifier.create(collector)
    .then(() -> {
        bus.publish(MessageEnvelope.from("alice", "Hello"));
        bus.publish(MessageEnvelope.from("bob", "Hi"));
    })
    .assertNext(list -> {
        // Assert on the collected messages
    })
    .verifyComplete();
```

**What's happening:**
1. `bus.stream()` subscribes to the hot stream
2. `take(2)` limits subscription to 2 messages
3. `collectList()` accumulates messages into a List<MessageEnvelope>
4. `StepVerifier` is a testing utility that:
   - Subscribes to the reactive chain
   - Executes the `.then()` block (publishes messages)
   - Waits for the stream to complete
   - Verifies expected values

## Reactive Operators Used

### take(n)
Limits the stream to the first N elements, then completes.

```java path=null start=null
bus.stream().take(2)  // Only receive first 2 messages
```

### collectList()
Terminal operator that collects all emitted values into a `Mono<List<T>>`.

```java path=null start=null
bus.stream().take(2).collectList()  // Returns Mono<List<MessageEnvelope>>
```

### Mono.zip()
Combines multiple Monos into a single Mono that emits a Tuple when all complete.

```java path=null start=null
Mono.zip(firstCollector, secondCollector)  // Wait for both to complete
```

## Message Flow Diagram

```
Publisher Thread          Sink (Hot)           Subscriber 1    Subscriber 2
     |                       |                      |               |
     |-- publish(msg1) ----->|                      |               |
     |                       |-----> msg1 -------->|               |
     |                       |-----> msg1 ----------------------->|
     |                       |                      |               |
     |-- publish(msg2) ----->|                      |               |
     |                       |-----> msg2 -------->|               |
     |                       |-----> msg2 ----------------------->|
     |                       |                      |               |
     |                    [New Subscriber 3 joins]                 |
     |                       |                                      |
     |-- publish(msg3) ----->|                      |               |
     |                       |-----> msg3 -------->|               |
     |                       |-----> msg3 ----------------------->|
     |                       |-----> msg3 ----------------------------> Subscriber 3
```

Notice: Subscriber 3 only receives msg3, not msg1 or msg2 (hot stream behavior).

## Key Behaviors Tested

### 1. Multiple Subscribers Receive All Messages
```java path=/Users/jettrocoenradie/Development/personal/agents-chatter/event-bus/src/test/java/org/rag4j/chatter/eventbus/bus/ReactorMessageBusTests.java start=16
    void distributesMessagesToMultipleSubscribers() {
        var bus = new ReactorMessageBus();
        var firstCollector = bus.stream().take(2).collectList();
        var secondCollector = bus.stream().take(2).collectList();
```

Both collectors receive the same messages because the sink uses `multicast()`.

### 2. Publishing Without Subscribers Fails
```java path=/Users/jettrocoenradie/Development/personal/agents-chatter/event-bus/src/test/java/org/rag4j/chatter/eventbus/bus/ReactorMessageBusTests.java start=40
    void publishFailsWhenNoSubscribers() {
        var bus = new ReactorMessageBus();
        var message = MessageEnvelope.from("system", "No listeners yet");

        assertThat(bus.publish(message)).isFalse();
    }
```

Because `directAllOrNothing()` fails when no active subscribers exist.

### 3. Late Subscribers Don't Receive Historical Messages
```java path=/Users/jettrocoenradie/Development/personal/agents-chatter/event-bus/src/test/java/org/rag4j/chatter/eventbus/bus/ReactorMessageBusTests.java start=55
    void lateSubscribersOnlyReceiveNewMessages() {
        var bus = new ReactorMessageBus();
        var earlyCollector = bus.stream().take(1).collectList();

        StepVerifier.create(earlyCollector)
            .then(() -> bus.publish(MessageEnvelope.from("early", "early message")))
            .assertNext(list -> assertThat(list).hasSize(1))
            .verifyComplete();

        var lateCollector = bus.stream().take(1).collectList();

        StepVerifier.create(lateCollector)
            .then(() -> bus.publish(MessageEnvelope.from("late", "late message")))
            .assertNext(list -> assertThat(list)
                .singleElement()
                .extracting(MessageEnvelope::author)
                .isEqualTo("late"))
            .verifyComplete();
    }
```

The late subscriber only receives "late message", confirming hot stream semantics.

## Common Reactor Patterns

### Subscribing to the Stream
```java path=null start=null
bus.stream()
    .subscribe(
        message -> System.out.println("Received: " + message.payload()),
        error -> System.err.println("Error: " + error),
        () -> System.out.println("Stream completed")
    );
```

### Filtering Messages
```java path=null start=null
bus.stream()
    .filter(msg -> msg.author().equals("alice"))
    .subscribe(msg -> System.out.println("Alice said: " + msg.payload()));
```

### Transforming Messages
```java path=null start=null
bus.stream()
    .map(msg -> msg.payload().toUpperCase())
    .subscribe(System.out::println);
```

### Handling Backpressure
```java path=null start=null
bus.stream()
    .onBackpressureDrop(dropped -> logger.warn("Dropped: {}", dropped))
    .subscribe(message -> processSlowly(message));
```

## Why Reactor for This Use Case?

1. **Non-blocking**: Handles many concurrent subscribers efficiently
2. **Backpressure**: Subscribers can signal they're overwhelmed
3. **Composable**: Easy to add filtering, transformation, error handling
4. **Spring Integration**: Works seamlessly with WebFlux for SSE/WebSocket
5. **Testing**: StepVerifier makes testing async code straightforward

## Further Reading

- [Project Reactor Documentation](https://projectreactor.io/docs/core/release/reference/)
- [Reactive Streams Specification](https://www.reactive-streams.org/)
- [Sinks Documentation](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Sinks.html)
