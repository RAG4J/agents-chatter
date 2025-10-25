"use client";

import { getApiBase } from "@/lib/api/messages";

export type ModerationEventDto = {
  threadId: string;
  agent: string;
  rationale: string;
  occurredAt: string;
  messagePreview?: string | null;
  attemptedDepth?: number | null;
};

export function createModerationEventSource(
  onEvent: (event: ModerationEventDto) => void,
  onError?: (error: Event) => void
): EventSource | null {
  if (typeof window === "undefined") {
    return null;
  }

  const source = new EventSource(`${getApiBase()}/moderation/events`);
  source.onmessage = (message) => {
    try {
      const data = JSON.parse(message.data) as ModerationEventDto;
      onEvent(data);
    } catch (error) {
      console.warn("Failed to parse moderation event", error);
    }
  };
  if (onError) {
    source.onerror = onError;
  }
  return source;
}
