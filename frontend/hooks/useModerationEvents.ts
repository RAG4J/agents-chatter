"use client";

import { useEffect, useMemo, useState } from "react";

import {
  createModerationEventSource,
  type ModerationEventDto
} from "@/lib/api/moderation";

export interface ModerationEvent {
  threadId: string;
  agent: string;
  rationale: string;
  occurredAt: Date;
  messagePreview?: string;
  attemptedDepth?: number;
}

export function useModerationEvents(limit = 25) {
  const [events, setEvents] = useState<ModerationEvent[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const source = createModerationEventSource(
      (dto) => {
        const event = mapDtoToEvent(dto);
        setEvents((current) => {
          const next = [event, ...current];
          return next.slice(0, limit);
        });
      },
      () => setError("Lost connection to moderation stream.")
    );

    return () => {
      if (source) {
        source.close();
      }
    };
  }, [limit]);

  const clearError = useMemo(
    () => () => setError(null),
    []
  );

  return { events, error, clearError };
}

function mapDtoToEvent(dto: ModerationEventDto): ModerationEvent {
  return {
    threadId: dto.threadId,
    agent: dto.agent,
    rationale: dto.rationale,
    occurredAt: new Date(dto.occurredAt),
    messagePreview: dto.messagePreview ?? undefined,
    attemptedDepth:
      dto.attemptedDepth === null || dto.attemptedDepth === undefined
        ? undefined
        : dto.attemptedDepth
  };
}
