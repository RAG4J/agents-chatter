package org.rag4j.chatter.eventbus.bus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.rag4j.chatter.core.message.MessageEnvelope;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class InMemoryMessageBusTests {

    @Test
    void distributesMessagesToMultipleSubscribers() {
        var bus = new InMemoryMessageBus();
        var firstCollector = bus.stream().take(2).collectList();
        var secondCollector = bus.stream().take(2).collectList();

        StepVerifier.create(Mono.zip(firstCollector, secondCollector))
            .then(() -> {
                bus.publish(MessageEnvelope.from("alice", "Hello"));
                bus.publish(MessageEnvelope.from("bob", "Hi"));
            })
            .assertNext(tuple -> {
                assertThat(tuple.getT1())
                    .hasSize(2)
                    .extracting(MessageEnvelope::author)
                    .containsExactly("alice", "bob");
                assertThat(tuple.getT2())
                    .hasSize(2)
                    .extracting(MessageEnvelope::author)
                    .containsExactly("alice", "bob");
            })
            .verifyComplete();
    }

    @Test
    void publishFailsWhenNoSubscribers() {
        var bus = new InMemoryMessageBus();
        var message = MessageEnvelope.from("system", "No listeners yet");

        assertThat(bus.publish(message)).isFalse();
    }

    @Test
    void rejectsNullMessages() {
        var bus = new InMemoryMessageBus();
        assertThatThrownBy(() -> bus.publish(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void lateSubscribersOnlyReceiveNewMessages() {
        var bus = new InMemoryMessageBus();
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
}
