package org.rag4j.chatter.web.agents;

import org.rag4j.chatter.application.port.in.AgentMessageSubscriptionPort;
import org.rag4j.chatter.application.port.in.AgentRegistrationUseCase;
import org.rag4j.chatter.application.port.in.PresencePort;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class AgentsConfig {

    @Bean
    @Profile("echo-agent")
    public EchoAgent echoAgent(AgentMessageSubscriptionPort subscriptionPort,
                               AgentPublisher agentPublisher,
                               AgentRegistrationUseCase agentRegistry,
                               PresencePort presencePort) {
        return new EchoAgent(subscriptionPort, agentPublisher, agentRegistry, presencePort);
    }

    @Bean
    @Profile("football-agent")
    public FootballAgent footballAgent(ChatModel chatModel,
                                      ChatMemory chatMemory,
                                      AgentMessageSubscriptionPort subscriptionPort,
                                      AgentPublisher agentPublisher,
                                      AgentRegistrationUseCase agentRegistry,
                                      PresencePort presencePort) {

        return new FootballAgent(ChatClient.builder(chatModel).build(), chatMemory, subscriptionPort, agentPublisher, agentRegistry, presencePort);
    }

    @Bean
    @Profile("apeldoornit-agent")
    public ApeldoornITScheduleAgent apeldoornITScheduleAgent(ChatModel chatModel,
                                                             ChatMemory chatMemory,
                                                             AgentMessageSubscriptionPort subscriptionPort,
                                                             AgentPublisher agentPublisher,
                                                             AgentRegistrationUseCase agentRegistry,
                                                             PresencePort presencePort) {
        return new ApeldoornITScheduleAgent(subscriptionPort, agentPublisher, agentRegistry, presencePort,
                ChatClient.builder(chatModel).build(), chatMemory);
    }

    @Bean
    @Profile("starwars-agent")
    public StarWarsAgent starWarsAgent(ChatModel chatModel,
                                       ChatMemory chatMemory,
                                       AgentMessageSubscriptionPort subscriptionPort,
                                       AgentPublisher agentPublisher,
                                       AgentRegistrationUseCase agentRegistry,
                                       PresencePort presencePort) {
        return new StarWarsAgent(ChatClient.builder(chatModel).build(), chatMemory, subscriptionPort, agentPublisher, agentRegistry, presencePort);
    }

    @Bean
    @Profile("startrek-agent")
    public StarTrekAgent starTrekAgent(ChatModel chatModel,
                                       ChatMemory chatMemory,
                                       AgentMessageSubscriptionPort subscriptionPort,
                                       AgentPublisher agentPublisher,
                                       AgentRegistrationUseCase agentRegistry,
                                       PresencePort presencePort) {
        return new StarTrekAgent(ChatClient.builder(chatModel).build(), chatMemory, subscriptionPort, agentPublisher, agentRegistry, presencePort);
    }

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder().build();
    }

}
