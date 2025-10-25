package org.rag4j.chatter.web.messages;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import jakarta.annotation.PreDestroy;

import org.rag4j.chatter.application.port.in.MessageStreamPort;
import org.rag4j.chatter.application.port.out.MessagePublicationPort;
import org.rag4j.chatter.domain.message.MessageEnvelope;
import org.rag4j.chatter.eventbus.bus.MessageBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import reactor.core.Disposable;

/**
 * Tracks chat messages in-memory and bridges publishers to the shared message bus.
 */
@Service
public class MessageService implements MessagePublicationPort, MessageStreamPort {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    private final MessageBus messageBus;
    private final List<MessageEnvelope> history = new CopyOnWriteArrayList<>();
    private final Disposable subscription;
    private final java.util.concurrent.CopyOnWriteArrayList<MessageStreamSubscriber> subscribers = new java.util.concurrent.CopyOnWriteArrayList<>();

    public MessageService(MessageBus messageBus) {
        this.messageBus = messageBus;
        this.subscription = messageBus.stream().subscribe(envelope -> {
            history.add(envelope);
            notifySubscribers(envelope);
        });
    }

    public List<MessageEnvelope> getHistory() {
        return List.copyOf(history);
    }

    public MessageEnvelope publish(String author, String payload) {
        MessageEnvelope envelope = MessageEnvelope.from(author, payload);
        return publish(envelope);
    }

    @Override
    public MessageEnvelope publish(MessageEnvelope envelope) {
        boolean accepted = messageBus.publish(envelope);
        if (!accepted) {
            history.add(envelope);
            notifySubscribers(envelope);
        }
        return envelope;
    }

    @Override
    public List<MessageEnvelope> history() {
        return getHistory();
    }

    @Override
    public MessageStreamSubscription subscribe(MessageStreamSubscriber subscriber) {
        subscribers.add(subscriber);
        return () -> subscribers.remove(subscriber);
    }

    private void notifySubscribers(MessageEnvelope envelope) {
        subscribers.forEach(subscriber -> {
            try {
                subscriber.onMessage(envelope);
            }
            catch (Exception ex) {
                logger.warn("Message stream subscriber threw exception: {}", ex.getMessage(), ex);
            }
        });
    }

    @PreDestroy
    void shutdown() {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
    }
}
