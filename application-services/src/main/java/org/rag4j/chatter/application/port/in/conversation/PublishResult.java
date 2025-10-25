package org.rag4j.chatter.application.port.in.conversation;

import java.util.UUID;

import org.rag4j.chatter.domain.message.MessageEnvelope;

/**
 * Result returned from the message publishing use case. Provides either the accepted envelope
 * or the reason a publication was rejected.
 */
public sealed interface PublishResult permits PublishResult.Accepted, PublishResult.Rejected {

    static PublishResult accepted(MessageEnvelope envelope) {
        return new Accepted(envelope);
    }

    static PublishResult rejected(UUID threadId, int attemptedDepth, String reason) {
        return new Rejected(threadId, attemptedDepth, reason);
    }

    /**
     * Represents a successful publication returning the envelope that entered the bus/store.
     */
    record Accepted(MessageEnvelope envelope) implements PublishResult {
    }

    /**
     * Represents a publication that was denied, carrying the attempted depth/thread for context.
     */
    record Rejected(UUID threadId, int attemptedDepth, String reason) implements PublishResult {
    }
}
