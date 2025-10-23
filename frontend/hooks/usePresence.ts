"use client";

import { useCallback, useEffect, useState } from "react";

import {
  fetchPresence,
  subscribePresence,
  type PresenceDto
} from "@/lib/api/presence";

export interface PresenceState {
    participants: PresenceDto[];
    loading: boolean;
    error: string | null;
}

export function usePresence() {
    const [participants, setParticipants] = useState<PresenceDto[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const refresh = useCallback(async () => {
        try {
            const data = await fetchPresence();
            setParticipants(data);
            setError(null);
        } catch (err) {
            setError(err instanceof Error ? err.message : "Failed to load presence");
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        let unsubscribe: (() => void) | undefined;
        refresh().then(() => {
            unsubscribe = subscribePresence(setParticipants, () => {
                setError("Realtime presence stream disconnected. Retrying…");
                setTimeout(refresh, 2000);
            });
        });
        return () => {
            unsubscribe?.();
        };
    }, [refresh]);

    return {
        participants,
        loading,
        error
    } satisfies PresenceState;
}
