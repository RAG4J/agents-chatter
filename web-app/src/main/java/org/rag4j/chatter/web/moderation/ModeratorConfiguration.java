package org.rag4j.chatter.web.moderation;

import java.time.Clock;

import org.rag4j.chatter.core.moderation.ModeratorService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ModeratorProperties.class)
public class ModeratorConfiguration {

    @Bean
    public Clock moderatorClock() {
        return Clock.systemUTC();
    }

    @Bean
    public ModeratorService moderatorService(
            ModeratorProperties properties,
            Clock moderatorClock,
            ModerationEventPublisher eventPublisher) {
        return new RuleBasedModeratorService(properties, moderatorClock, eventPublisher);
    }
}
