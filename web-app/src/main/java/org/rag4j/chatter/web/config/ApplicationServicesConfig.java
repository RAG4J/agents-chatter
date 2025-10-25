package org.rag4j.chatter.web.config;

import org.rag4j.chatter.application.agents.AgentRegistryServices;
import org.rag4j.chatter.application.agents.AgentRegistryServices.AgentRegistryUseCases;
import org.rag4j.chatter.application.messages.ConversationServices;
import org.rag4j.chatter.application.messages.ConversationServices.ConversationServiceBundle;
import org.rag4j.chatter.application.port.in.AgentDiscoveryPort;
import org.rag4j.chatter.application.port.in.AgentRegistrationUseCase;
import org.rag4j.chatter.application.port.in.conversation.AgentMessagingCallback;
import org.rag4j.chatter.application.port.in.conversation.ConversationUseCase;
import org.rag4j.chatter.application.port.out.AgentRegistrationPort;
import org.rag4j.chatter.application.port.out.MessagePublicationPort;
import org.rag4j.chatter.application.port.out.ModerationEventPort;
import org.rag4j.chatter.application.port.out.ModerationPolicyPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationServicesConfig {

    @Bean
    ConversationServiceBundle conversationServiceBundle(
            MessagePublicationPort messagePublicationPort,
            ModerationPolicyPort moderationPolicyPort,
            ModerationEventPort moderationEventPort,
            @Value("${conversation.max-agent-depth:2}") int maxAgentDepth) {
        return ConversationServices.create(
                messagePublicationPort,
                moderationPolicyPort,
                moderationEventPort,
                maxAgentDepth);
    }

    @Bean
    ConversationUseCase conversationUseCase(ConversationServiceBundle bundle) {
        return bundle.conversationUseCase();
    }

    @Bean
    AgentMessagingCallback agentMessagingCallback(ConversationServiceBundle bundle) {
        return bundle.agentMessagingCallback();
    }

    @Bean
    AgentRegistryUseCases agentRegistryUseCases(AgentRegistrationPort registrationPort) {
        return AgentRegistryServices.create(registrationPort);
    }

    @Bean
    AgentRegistrationUseCase agentRegistrationUseCase(AgentRegistryUseCases useCases) {
        return useCases.registrationUseCase();
    }

    @Bean
    AgentDiscoveryPort agentDiscoveryPort(AgentRegistryUseCases useCases) {
        return useCases.discoveryPort();
    }
}
