package org.rag4j.chatter.web.agents;

import org.rag4j.chatter.core.message.MessageEnvelope;
import reactor.core.publisher.Mono;

/**
 * Core agent interface representing pure business logic for message processing.
 * Agents implementing this interface focus solely on transforming input messages
 * into responses, without concern for infrastructure details like subscriptions,
 * lifecycle management, or presence tracking.
 *
 * <p>The framework handles all cross-cutting concerns through {@link AgentLifecycleManager}.
 */
public interface Agent {

    /**
     * Placeholder value agents can return to indicate they have nothing to say.
     * Responses containing this placeholder will be suppressed.
     */
    String NO_MESSAGE_PLACEHOLDER = "#nothingtosay#";

    /**
     * Returns the unique name identifying this agent.
     *
     * @return the agent's display name
     */
    String name();

    /**
     * Processes an incoming message payload and returns a response.
     * Agents should return:
     * <ul>
     *   <li>A non-empty string with the response content</li>
     *   <li>An empty string or {@link #NO_MESSAGE_PLACEHOLDER} if the agent has nothing to contribute</li>
     *   <li>{@code Mono.empty()} if the message should be ignored</li>
     * </ul>
     *
     * @param payload the incoming message text
     * @return a Mono emitting the response, empty response, or empty if ignored
     */
    Mono<String> processMessage(String payload);

    /**
     * Processes a complete message envelope and returns a response.
     * Default implementation delegates to {@link #processMessage(String)}.
     * Agents can override this to access envelope metadata (thread ID, author, etc.).
     *
     * @param envelope the complete message envelope
     * @return a Mono emitting the response, empty response, or empty if ignored
     */
    default Mono<String> processMessage(MessageEnvelope envelope) {
        return processMessage(envelope.payload());
    }
}
