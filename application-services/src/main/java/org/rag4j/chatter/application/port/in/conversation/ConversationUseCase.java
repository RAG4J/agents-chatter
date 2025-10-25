package org.rag4j.chatter.application.port.in.conversation;

/**
 * High-level use case for publishing messages into a conversation.
 */
public interface ConversationUseCase {

    PublishResult publish(PublishCommand command);
}
