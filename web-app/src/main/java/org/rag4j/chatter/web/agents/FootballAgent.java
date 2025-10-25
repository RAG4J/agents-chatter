package org.rag4j.chatter.web.agents;

import org.rag4j.chatter.application.port.in.AgentMessageSubscriptionPort;
import org.rag4j.chatter.application.port.in.AgentRegistrationUseCase;
import org.rag4j.chatter.application.port.in.PresencePort;
import org.rag4j.chatter.domain.presence.PresenceRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class FootballAgent extends SubscriberAgent {
    private static final Logger logger = LoggerFactory.getLogger(FootballAgent.class);

    public static final String AGENT_NAME = "Football Agent";

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public FootballAgent(ChatClient chatClient,
                         ChatMemory chatMemory,
                         AgentMessageSubscriptionPort subscriptionPort,
                         AgentPublisher agentPublisher,
                         AgentRegistrationUseCase agentRegistry,
                         PresencePort presencePort) {
        super(AGENT_NAME, PresenceRole.AGENT, subscriptionPort, agentPublisher, agentRegistry, presencePort);
        this.chatClient = chatClient;
        this.chatMemory = chatMemory;
    }

    @Override
    Logger logger() {
        return logger;
    }

    @Override
    Mono<String> messagePayload(String incomingPayload) {
        String prompt = """
                You are an AI agent that knows everything about Football.
                If you see a message about Football, you will answer it correctly.
                Feel free to answer any question about Football teams, players, matches, scores, history, and statistics.
                Always reply in short answers.
                If the message is not about Football, respond with the exact placeholder "#nothingtosay#" with no additional text.
                """;

        String userMessage = String.format("""
                Here is the message to answer:
                %s
                """, incomingPayload);

        return Mono.fromCallable(() -> this.chatClient.prompt()
                .system(prompt)
                .user(userMessage)
                .advisors(MessageChatMemoryAdvisor.builder(this.chatMemory).conversationId(AGENT_NAME).build())
                .call()
                .content()).subscribeOn(Schedulers.boundedElastic());
    }
}
