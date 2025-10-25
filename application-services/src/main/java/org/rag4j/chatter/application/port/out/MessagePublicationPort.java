package org.rag4j.chatter.application.port.out;

import org.rag4j.chatter.domain.message.MessageEnvelope;

/**
 * Outbound port responsible for persisting or broadcasting a message envelope to the
 * delivery infrastructure (e.g. Reactor bus, message broker).
 */
public interface MessagePublicationPort {

    /**
     * Publish the given envelope, returning the final envelope that should be visible to
     * downstream consumers. Implementations may enrich metadata before returning.
     */
    MessageEnvelope publish(MessageEnvelope envelope);
}
