import { act, renderHook } from "@testing-library/react";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import type { ModerationEventDto } from "@/lib/api/moderation";
import { useModerationEvents } from "@/hooks/useModerationEvents";

describe("useModerationEvents", () => {
  const instances: MockEventSource[] = [];

  beforeEach(() => {
    instances.length = 0;
    vi.stubGlobal(
      "EventSource",
      class implements EventSource {
        url: string;
        readyState = 0;
        withCredentials = false;
        onopen: ((this: EventSource, ev: Event) => unknown) | null = null;
        onmessage: ((this: EventSource, ev: MessageEvent) => unknown) | null =
          null;
        onerror: ((this: EventSource, ev: Event) => unknown) | null = null;
        constructor(url: string) {
          this.url = url;
          instances.push(this as unknown as MockEventSource);
        }
        close(): void {
          this.readyState = 2;
        }
        addEventListener(): void {
          // no-op for tests
        }
        removeEventListener(): void {
          // no-op for tests
        }
        dispatchEvent(): boolean {
          return true;
        }
      }
    );
  });

  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it("captures incoming moderation events", () => {
    const { result } = renderHook(() => useModerationEvents(5));

    expect(instances).toHaveLength(1);
    const eventSource = instances[0];

    const sample: ModerationEventDto = {
      threadId: "thread-1",
      agent: "Echo Agent",
      rationale: "Duplicate payload",
      occurredAt: "2025-10-24T10:00:00Z",
      messagePreview: "echo hello",
      attemptedDepth: 2
    };

    act(() => {
      eventSource.onmessage?.({ data: JSON.stringify(sample) } as MessageEvent);
    });

    expect(result.current.events).toHaveLength(1);
    expect(result.current.events[0].agent).toBe("Echo Agent");
    expect(result.current.events[0].attemptedDepth).toBe(2);
  });

  it("handles stream errors", () => {
    const { result } = renderHook(() => useModerationEvents());

    const eventSource = instances[0];
    act(() => {
      eventSource.onerror?.(new Event("error"));
    });

    expect(result.current.error).toMatch(/moderation stream/i);

    act(() => {
      result.current.clearError();
    });

    expect(result.current.error).toBeNull();
  });
});

type MockEventSource = EventSource & {
  onmessage:
    | ((this: EventSource, ev: MessageEvent<unknown>) => unknown)
    | null;
  onerror: ((this: EventSource, ev: Event) => unknown) | null;
};
