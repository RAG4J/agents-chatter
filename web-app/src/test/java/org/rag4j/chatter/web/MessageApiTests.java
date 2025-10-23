package org.rag4j.chatter.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;

import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MessageApiTests {

    @Autowired
    WebTestClient webTestClient;

    @LocalServerPort
    int port;

    @Test
    void postMessageReturnsCreatedPayload() {
        webTestClient.post()
            .uri("/api/messages")
            .bodyValue(new MessageRequest("alice", "Hello world"))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.author").isEqualTo("alice")
            .jsonPath("$.payload").isEqualTo("Hello world");
    }

    @Test
    void getMessagesReturnsHistory() {
        webTestClient.post()
            .uri("/api/messages")
            .bodyValue(new MessageRequest("bob", "Hi there"))
            .exchange()
            .expectStatus().isOk();

        webTestClient.get()
            .uri("/api/messages")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(MessageResponse.class)
            .value(list -> assertThat(list)
                .isNotEmpty()
                .allMatch(message -> message.author() != null));
    }

    @Test
    void websocketBroadcastsMessages() {
        ReactorNettyWebSocketClient client = new ReactorNettyWebSocketClient();
        List<String> received = new CopyOnWriteArrayList<>();

        client.execute(
            URI.create("ws://localhost:" + port + "/ws/messages"),
            session -> {
                Mono<Void> send = session.send(
                    Mono.just(session.textMessage("{\"author\":\"socket\",\"payload\":\"Hi\"}")));
                Mono<Void> receive = session.receive()
                    .map(msg -> msg.getPayloadAsText())
                    .doOnNext(received::add)
                    .take(1)
                    .then();
                return Mono.when(send, receive);
            })
            .block(Duration.ofSeconds(5));

        assertThat(received).isNotEmpty();
        assertThat(received.getFirst()).contains("\"author\":\"socket\"");
    }

    private record MessageRequest(String author, String payload) {
    }

    private record MessageResponse(String id, String author, String payload, String timestamp) {
    }
}
