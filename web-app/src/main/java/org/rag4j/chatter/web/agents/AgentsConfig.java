package org.rag4j.chatter.web.agents;

import org.rag4j.chatter.web.messages.MessageService;
import org.rag4j.chatter.web.presence.PresenceService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentsConfig {

    @Bean
    public EchoAgent echoAgent(MessageService messageService, AgentPublisher agentPublisher, PresenceService presenceService) {
        return new EchoAgent(messageService, agentPublisher, presenceService);
    }

    @Bean
    public FootballAgent footballAgent(ChatModel chatModel,
                                       ChatMemory chatMemory,
                                       MessageService messageService,
                                       AgentPublisher agentPublisher,
                                       PresenceService presenceService) {

        return new FootballAgent(ChatClient.builder(chatModel).build(), chatMemory, messageService, agentPublisher, presenceService);
    }

    @Bean
    public ApeldoornITScheduleAgent apeldoornITScheduleAgent(ChatModel chatModel,
                                                             ChatMemory chatMemory,
                                                             MessageService messageService,
                                                             AgentPublisher agentPublisher,
                                                             PresenceService presenceService) {
        return new ApeldoornITScheduleAgent(messageService, agentPublisher, presenceService,
                ChatClient.builder(chatModel).build(), chatMemory);
    }

    @Bean
    public StarWarsAgent starWarsAgent(ChatModel chatModel,
                                       ChatMemory chatMemory,
                                       MessageService messageService,
                                       AgentPublisher agentPublisher,
                                       PresenceService presenceService) {
        return new StarWarsAgent(ChatClient.builder(chatModel).build(), chatMemory, messageService, agentPublisher, presenceService);
    }

    @Bean
    public StarTrekAgent starTrekAgent(ChatModel chatModel,
                                       ChatMemory chatMemory,
                                       MessageService messageService,
                                       AgentPublisher agentPublisher,
                                       PresenceService presenceService) {
        return new StarTrekAgent(ChatClient.builder(chatModel).build(), chatMemory, messageService, agentPublisher, presenceService);
    }


    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder().build();
    }

}
