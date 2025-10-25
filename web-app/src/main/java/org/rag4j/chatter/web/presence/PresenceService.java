package org.rag4j.chatter.web.presence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.rag4j.chatter.application.port.in.PresencePort;
import org.rag4j.chatter.application.port.in.PresencePort.PresenceSubscriber;
import org.rag4j.chatter.application.port.in.PresencePort.PresenceSubscription;
import org.rag4j.chatter.domain.presence.PresenceParticipant;
import org.rag4j.chatter.domain.presence.PresenceRole;
import org.rag4j.chatter.domain.presence.PresenceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

@Service
public class PresenceService implements PresencePort {

    private static final Logger logger = LoggerFactory.getLogger(PresenceService.class);

    private final Map<String, PresenceEntry> participants = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<PresenceSubscriber> subscribers = new CopyOnWriteArrayList<>();

    public PresenceService(ChatParticipantsProperties properties) {
        properties.getAgents().forEach(agent -> participants.putIfAbsent(
                normalize(agent.name()),
                new PresenceEntry(new PresenceParticipant(agent.name(), agent.role()))));
        var human = properties.getHuman();
        participants.putIfAbsent(normalize(human.name()),
                new PresenceEntry(new PresenceParticipant(human.name(), human.role())));
        notifySubscribers();
    }

    @Override
    public void markOnline(String name, PresenceRole role) {
        PresenceEntry entry = participants.computeIfAbsent(
                normalize(name),
                key -> new PresenceEntry(new PresenceParticipant(name, role)));
        entry.increment();
        logger.debug("Presence online: {} (count={})", entry.participant().name(), entry.count());
        notifySubscribers();
    }

    @Override
    public void markOffline(String name) {
        PresenceEntry entry = participants.get(normalize(name));
        if (entry == null) {
            return;
        }
        entry.decrement();
        logger.debug("Presence offline event: {} (count={})", entry.participant().name(), entry.count());
        notifySubscribers();
    }

    @Override
    public List<PresenceStatus> snapshot() {
        return currentStatuses();
    }

    @Override
    public PresenceSubscription subscribe(PresenceSubscriber subscriber) {
        subscribers.add(subscriber);
        subscriber.onUpdate(currentStatuses());
        return () -> subscribers.remove(subscriber);
    }

    /**
     * Exposes a Reactor-friendly stream for Web/SSE adapters.
     */
    public Flux<List<PresenceStatus>> stream() {
        return Flux.create(emitter -> {
            PresenceSubscription subscription = subscribe(emitter::next);
            emitter.onDispose(subscription::close);
        });
    }

    private List<PresenceStatus> currentStatuses() {
        List<PresenceStatus> statuses = new ArrayList<>();
        for (PresenceEntry entry : participants.values()) {
            statuses.add(new PresenceStatus(entry.participant(), entry.count() > 0, entry.count()));
        }
        statuses.sort((a, b) -> {
            int roleCompare = a.participant().role().compareTo(b.participant().role());
            if (roleCompare != 0) {
                return roleCompare;
            }
            return a.participant().name().compareToIgnoreCase(b.participant().name());
        });
        return Collections.unmodifiableList(statuses);
    }

    private void notifySubscribers() {
        List<PresenceStatus> snapshot = currentStatuses();
        for (PresenceSubscriber subscriber : subscribers) {
            try {
                subscriber.onUpdate(snapshot);
            }
            catch (Exception ex) {
                logger.warn("Presence subscriber threw exception: {}", ex.getMessage(), ex);
            }
        }
    }

    private static String normalize(String name) {
        return name == null ? "" : name.trim().toLowerCase(Locale.ENGLISH);
    }

    private static final class PresenceEntry {
        private final PresenceParticipant participant;
        private int count;

        private PresenceEntry(PresenceParticipant participant) {
            this.participant = participant;
            this.count = 0;
        }

        private PresenceParticipant participant() {
            return participant;
        }

        private synchronized void increment() {
            count++;
        }

        private synchronized void decrement() {
            if (count > 0) {
                count--;
            }
        }

        private synchronized int count() {
            return count;
        }
    }
}
