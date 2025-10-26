package org.rag4j.chatter.web.messages;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import jakarta.annotation.PreDestroy;

import org.rag4j.chatter.eventbus.bus.MessageBus;
import org.rag4j.chatter.core.message.MessageEnvelope;
import org.springframework.stereotype.Service;

import reactor.core.Disposable;

/**
 * Tracks chat messages in-memory and bridges publishers to the shared message bus.
 */
@Service
public class MessageService {

    private final MessageBus messageBus;
    private final List<MessageEnvelope> history = new CopyOnWriteArrayList<>();
    private final Disposable subscription;

    public MessageService(MessageBus messageBus) {
        this.messageBus = messageBus;
        this.subscription = messageBus.stream().subscribe(history::add);
    }

    public List<MessageEnvelope> getHistory() {
        return List.copyOf(history);
    }

    public MessageEnvelope publish(String author, String payload) {
        MessageEnvelope envelope = MessageEnvelope.from(author, payload);
        return publish(envelope);
    }

    public MessageEnvelope publish(MessageEnvelope envelope) {
        boolean accepted = messageBus.publish(envelope);
        if (!accepted) {
            history.add(envelope);
        }
        return envelope;
    }

    public reactor.core.publisher.Flux<MessageEnvelope> stream() {
        return messageBus.stream();
    }

    public void clearHistory() {
        history.clear();
    }

    @PreDestroy
    void shutdown() {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
    }
}
