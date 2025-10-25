package org.rag4j.chatter.application.port.out;

import org.rag4j.chatter.domain.moderation.ModerationEvent;

/**
 * Outbound port for emitting moderation telemetry to interested subscribers
 * (e.g. SSE stream, metrics, logging).
 */
public interface ModerationEventPort {

    /**
     * Publish the supplied moderation event to the configured sink.
     */
    void publish(ModerationEvent event);
}
