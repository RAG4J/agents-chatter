package org.rag4j.chatter.web.presence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class PresenceService {

    private static final Logger logger = LoggerFactory.getLogger(PresenceService.class);

    private final Map<String, PresenceEntry> participants = new ConcurrentHashMap<>();
    private final Sinks.Many<List<PresenceStatus>> sink = Sinks.many().replay().latest();

    public PresenceService(ChatParticipantsProperties properties) {
        properties.getAgents().forEach(agent -> participants.putIfAbsent(
            normalize(agent.name()), new PresenceEntry(new PresenceParticipant(agent.name(), agent.role()))));
        var human = properties.getHuman();
        participants.putIfAbsent(normalize(human.name()), new PresenceEntry(new PresenceParticipant(human.name(), human.role())));
        emitSnapshot();
    }

    public void markOnline(String name, PresenceRole role) {
        PresenceEntry entry = participants.computeIfAbsent(normalize(name), key -> new PresenceEntry(new PresenceParticipant(name, role)));
        entry.increment();
        logger.debug("Presence online: {} (count={})", entry.participant().name(), entry.count());
        emitSnapshot();
    }

    public void markOffline(String name) {
        PresenceEntry entry = participants.get(normalize(name));
        if (entry == null) {
            return;
        }
        entry.decrement();
        logger.debug("Presence offline event: {} (count={})", entry.participant().name(), entry.count());
        emitSnapshot();
    }

    public List<PresenceStatus> snapshot() {
        return currentStatuses();
    }

    public Flux<List<PresenceStatus>> stream() {
        return sink.asFlux();
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

    private void emitSnapshot() {
        sink.tryEmitNext(currentStatuses());
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
