package org.rag4j.chatter.web.moderation;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "moderator")
public class ModeratorProperties {

    /**
     * Minimum delay between successive messages from the same agent.
     */
    private Duration agentCooldown = Duration.ofMillis(5);

    /**
     * Number of recent payloads per thread to remember for duplicate detection.
     */
    private int duplicateWindowSize = 20;

    /**
     * Number of agent alternations allowed before considering the exchange a loop (values ≥ 1).
     */
    private int loopAlternationsThreshold = 2;

    /**
     * Number of recent agent authors to keep in history for loop detection (defaults to 10).
     */
    private int authorHistorySize = 10;

    public Duration getAgentCooldown() {
        return agentCooldown;
    }

    public void setAgentCooldown(Duration agentCooldown) {
        if (agentCooldown == null || agentCooldown.isNegative()) {
            throw new IllegalArgumentException("agentCooldown must be non-negative");
        }
        this.agentCooldown = agentCooldown;
    }

    public int getDuplicateWindowSize() {
        return duplicateWindowSize;
    }

    public void setDuplicateWindowSize(int duplicateWindowSize) {
        if (duplicateWindowSize < 1) {
            throw new IllegalArgumentException("duplicateWindowSize must be >= 1");
        }
        this.duplicateWindowSize = duplicateWindowSize;
    }

    public int getLoopAlternationsThreshold() {
        return loopAlternationsThreshold;
    }

    public void setLoopAlternationsThreshold(int loopAlternationsThreshold) {
        if (loopAlternationsThreshold < 1) {
            throw new IllegalArgumentException("loopAlternationsThreshold must be >= 1");
        }
        this.loopAlternationsThreshold = loopAlternationsThreshold;
    }

    public int getAuthorHistorySize() {
        return authorHistorySize;
    }

    public void setAuthorHistorySize(int authorHistorySize) {
        if (authorHistorySize < 2) {
            throw new IllegalArgumentException("authorHistorySize must be >= 2");
        }
        this.authorHistorySize = authorHistorySize;
    }
}
