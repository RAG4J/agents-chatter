package org.rag4j.chatter.web.config;

import org.rag4j.chatter.application.agents.AgentRegistryService;
import org.rag4j.chatter.application.messages.ConversationApplicationService;
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
    ConversationApplicationService conversationApplicationService(
            MessagePublicationPort messagePublicationPort,
            ModerationPolicyPort moderationPolicyPort,
            ModerationEventPort moderationEventPort,
            @Value("${conversation.max-agent-depth:2}") int maxAgentDepth) {
        return new ConversationApplicationService(messagePublicationPort, moderationPolicyPort, moderationEventPort, maxAgentDepth);
    }

    @Bean
    AgentRegistryService agentRegistryService(AgentRegistrationPort registrationPort) {
        return new AgentRegistryService(registrationPort);
    }
}
