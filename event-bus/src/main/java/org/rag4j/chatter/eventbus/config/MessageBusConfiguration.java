package org.rag4j.chatter.eventbus.config;

import org.rag4j.chatter.core.message.MessageBus;
import org.rag4j.chatter.eventbus.bus.InMemoryMessageBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers the in-memory Reactor message bus.
 */
@Configuration
public class MessageBusConfiguration {

    @Bean
    public MessageBus messageBus() {
        return new InMemoryMessageBus();
    }
}
