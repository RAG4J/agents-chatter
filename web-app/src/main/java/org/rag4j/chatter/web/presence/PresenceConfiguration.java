package org.rag4j.chatter.web.presence;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ChatParticipantsProperties.class)
public class PresenceConfiguration {
}
