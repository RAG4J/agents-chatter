package org.rag4j.chatter.application.messages;

import org.rag4j.chatter.application.port.in.conversation.AgentMessagingCallback;
import org.rag4j.chatter.application.port.in.conversation.ConversationUseCase;
import org.rag4j.chatter.application.port.out.MessagePublicationPort;
import org.rag4j.chatter.application.port.out.ModerationEventPort;
import org.rag4j.chatter.application.port.out.ModerationPolicyPort;

public final class ConversationServices {

    private ConversationServices() {
    }

    public static ConversationServiceBundle create(
            MessagePublicationPort messagePublicationPort,
            ModerationPolicyPort moderationPolicyPort,
            ModerationEventPort moderationEventPort,
            int maxAgentDepth) {
        ConversationApplicationService service = new ConversationApplicationService(
                messagePublicationPort,
                moderationPolicyPort,
                moderationEventPort,
                maxAgentDepth);
        ConversationUseCase useCase = service::publish;
        AgentMessagingCallback callback = service;
        return new ConversationServiceBundle(useCase, callback);
    }

    public record ConversationServiceBundle(
            ConversationUseCase conversationUseCase,
            AgentMessagingCallback agentMessagingCallback) {
    }
}
