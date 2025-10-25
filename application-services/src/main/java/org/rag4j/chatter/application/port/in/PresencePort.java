package org.rag4j.chatter.application.port.in;

import java.util.List;

import org.rag4j.chatter.domain.presence.PresenceRole;
import org.rag4j.chatter.domain.presence.PresenceStatus;

/**
 * Inbound port encapsulating presence tracking operations for chat participants.
 */
public interface PresencePort {

    void markOnline(String participantName, PresenceRole role);

    void markOffline(String participantName);

    List<PresenceStatus> snapshot();

    PresenceSubscription subscribe(PresenceSubscriber subscriber);

    @FunctionalInterface
    interface PresenceSubscriber {
        void onUpdate(List<PresenceStatus> statuses);
    }

    interface PresenceSubscription extends AutoCloseable {
        @Override
        void close();

        default boolean isActive() {
            return true;
        }
    }
}
