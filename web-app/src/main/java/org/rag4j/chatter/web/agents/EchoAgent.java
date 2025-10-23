package org.rag4j.chatter.web.agents;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.rag4j.chatter.eventbus.bus.MessageEnvelope;
import org.rag4j.chatter.web.messages.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import reactor.core.Disposable;

public class EchoAgent {
    private static final Logger logger = LoggerFactory.getLogger(EchoAgent.class);

    public static final String AGENT_NAME = "Echo Agent";

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final MessageService messageService;

    private Disposable subscription;

    public EchoAgent(ChatClient chatClient, ChatMemory chatMemory, MessageService messageService) {
        this.chatClient = chatClient;
        this.chatMemory = chatMemory;
        this.messageService = messageService;
    }

    @PostConstruct
    public void subscribe() {
        logger.info("Registering EchoAgent subscriber");
        subscription = messageService.stream()
                .doOnNext(e -> logger.info("Received message from {}", e.author()))
                .filter(envelope -> !isEchoAgent(envelope))
                .doOnNext(this::publishEcho)
                .subscribe();
    }

    @PreDestroy
    public void shutdown() {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
        logger.info("EchoAgent subscriber disposed");
    }

    private void publishEcho(MessageEnvelope incoming) {
        String echoPayload = "echo " + incoming.payload();
        logger.info("Echoing message from {} as '{}'", incoming.author(), echoPayload);
        messageService.publish(EchoAgent.AGENT_NAME, echoPayload);
    }

    private boolean isEchoAgent(MessageEnvelope envelope) {
        logger.info("isEchoAgent: {}", EchoAgent.AGENT_NAME.equalsIgnoreCase(envelope.author()));
        return EchoAgent.AGENT_NAME.equalsIgnoreCase(envelope.author());
    }

}
