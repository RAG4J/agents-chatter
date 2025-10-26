package org.rag4j.chatter.web.moderation;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.rag4j.chatter.core.moderation.AgentMessageContext;
import org.rag4j.chatter.core.moderation.ModerationDecision;
import org.rag4j.chatter.core.moderation.ModerationEvent;
import org.rag4j.chatter.core.moderation.ModeratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleBasedModeratorService implements ModeratorService {

    private static final Logger logger = LoggerFactory.getLogger(RuleBasedModeratorService.class);

    private final ModeratorProperties properties;
    private final Clock clock;
    private final ModerationEventPublisher eventPublisher;
    private final ConcurrentMap<UUID, ThreadState> threadStates = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Instant> agentLastPublish = new ConcurrentHashMap<>();

    public RuleBasedModeratorService(ModeratorProperties properties, Clock clock, ModerationEventPublisher eventPublisher) {
        this.properties = properties;
        this.clock = clock;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public ModerationDecision evaluate(AgentMessageContext context) {
        Instant now = clock.instant();

        ModerationDecision cooldownDecision = enforceCooldown(context, now);
        if (cooldownDecision != null) {
            return cooldownDecision;
        }

        ThreadState state = threadStates.computeIfAbsent(context.threadId(), id -> new ThreadState());
        String normalizedPayload = normalize(context.payload());
        ModerationDecision duplicateDecision;
        ModerationDecision loopDecision;

        synchronized (state) {
            duplicateDecision = enforceDuplicatePayload(state, context, normalizedPayload);
            if (duplicateDecision != null) {
                return duplicateDecision;
            }

            loopDecision = enforceLoopSuppression(state, context);
            if (loopDecision != null) {
                return loopDecision;
            }

            updateStateOnApproval(state, context, normalizedPayload);
        }

        agentLastPublish.put(context.agentName(), now);
        return ModerationDecision.approve();
    }

    private ModerationDecision enforceCooldown(AgentMessageContext context, Instant now) {
        Instant last = agentLastPublish.get(context.agentName());
        Duration cooldown = properties.getAgentCooldown();
        if (last != null && Duration.between(last, now).compareTo(cooldown) < 0) {
            Duration remaining = cooldown.minus(Duration.between(last, now));
            String reason = "Cooldown active for agent '%s' (wait %d ms)".formatted(context.agentName(), remaining.toMillis());
            return reject(context, reason, Optional.of(context.agentReplyDepth()));
        }
        return null;
    }

    private ModerationDecision enforceDuplicatePayload(ThreadState state, AgentMessageContext context, String normalizedPayload) {
        if (state.recentPayloads.contains(normalizedPayload)) {
            String reason = "Duplicate payload detected for thread %s".formatted(context.threadId());
            return reject(context, reason, Optional.of(context.agentReplyDepth()));
        }
        return null;
    }

    private ModerationDecision enforceLoopSuppression(ThreadState state, AgentMessageContext context) {
        List<String> simulatedAuthors = new ArrayList<>(state.recentAgentAuthors);
        simulatedAuthors.add(context.agentName());
        if (isAlternatingBetweenTwoAgents(simulatedAuthors, properties.getLoopAlternationsThreshold())) {
            String reason = "Detected loop between agents near thread %s".formatted(context.threadId());
            return reject(context, reason, Optional.of(context.agentReplyDepth()));
        }
        return null;
    }

    private void updateStateOnApproval(ThreadState state, AgentMessageContext context, String normalizedPayload) {
        state.recentPayloads.addLast(normalizedPayload);
        if (state.recentPayloads.size() > properties.getDuplicateWindowSize()) {
            state.recentPayloads.removeFirst();
        }

        state.recentAgentAuthors.addLast(context.agentName());
        int maxAuthors = Math.max(properties.getAuthorHistorySize(), properties.getLoopAlternationsThreshold() * 2);
        while (state.recentAgentAuthors.size() > maxAuthors) {
            state.recentAgentAuthors.removeFirst();
        }
    }

    private boolean isAlternatingBetweenTwoAgents(List<String> authors, int alternationsThreshold) {
        int window = alternationsThreshold * 2;
        if (authors.size() < window) {
            return false;
        }
        List<String> windowAuthors = authors.subList(authors.size() - window, authors.size());
        String first = windowAuthors.get(0);
        String second = windowAuthors.get(1);
        if (first.equals(second)) {
            return false;
        }
        for (int i = 0; i < windowAuthors.size(); i++) {
            String expected = (i % 2 == 0) ? first : second;
            if (!windowAuthors.get(i).equals(expected)) {
                return false;
            }
        }
        return true;
    }

    private String normalize(String payload) {
        return payload == null ? "" : payload.trim().toLowerCase(Locale.ENGLISH);
    }

    private ModerationDecision reject(AgentMessageContext context, String reason, Optional<Integer> attemptedDepth) {
        logger.info(reason);
        String preview = context.payload();
        if (preview != null) {
            preview = preview.trim();
            if (preview.length() > 140) {
                preview = preview.substring(0, 140) + "…";
            }
        }
        eventPublisher.publish(ModerationEvent.rejection(
                context.threadId(),
                context.agentName(),
                reason,
                clock.instant(),
                preview == null || preview.isBlank() ? Optional.empty() : Optional.of(preview),
                attemptedDepth));
        return ModerationDecision.reject(reason);
    }

    private static final class ThreadState {
        private final Deque<String> recentPayloads = new ArrayDeque<>();
        private final Deque<String> recentAgentAuthors = new ArrayDeque<>();
    }
}
