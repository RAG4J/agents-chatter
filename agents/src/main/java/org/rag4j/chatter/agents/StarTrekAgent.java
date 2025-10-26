package org.rag4j.chatter.agents;

import org.rag4j.chatter.core.agent.Agent;
import jakarta.annotation.PostConstruct;
import org.rag4j.chatter.core.agent.AgentLifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class StarTrekAgent implements Agent {
    private static final Logger logger = LoggerFactory.getLogger(StarTrekAgent.class);

    public static final String AGENT_NAME = "StarTrek Agent";

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final AgentLifecycleManager lifecycleManager;

    public StarTrekAgent(ChatClient chatClient, ChatMemory chatMemory, AgentLifecycleManager lifecycleManager) {
        this.chatClient = chatClient;
        this.chatMemory = chatMemory;
        this.lifecycleManager = lifecycleManager;
    }

    @PostConstruct
    public void init() {
        lifecycleManager.subscribeAgent(this);
    }

    @Override
    public String name() {
        return AGENT_NAME;
    }

    @Override
    public Mono<String> processMessage(String payload) {
        logger.debug("StarTrekAgent processMessage: {}", payload);

        String prompt = """
                You are an AI agent that knows everything about StarTrek.
                If you see a message about Science Fiction, always talk about StarTrek and explain why StarTrek is better than anything else.
                Especially answer questions related to StarWars with reasons why StarTrek is better.
                Feel free to answer any question about StarTrek characters, ships, planets, movies, series, lore, and trivia.
                Always reply in short answers.
                If the message is not about StarTrek or Science Fiction, respond with the exact placeholder "#nothingtosay#" with no additional text.
                """;

        String userMessage = String.format("""
                Here is the message to answer:
                %s
                """, payload);

        return Mono.fromCallable(() -> chatClient.prompt()
                .system(prompt)
                .user(userMessage)
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory).conversationId(AGENT_NAME).build())
                .call()
                .content()).subscribeOn(Schedulers.boundedElastic());
    }
}
