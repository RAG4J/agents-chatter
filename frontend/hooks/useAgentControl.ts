"use client";

import { useCallback, useState } from "react";

import {
  activateAgent,
  deactivateAgent,
  type PresenceDto
} from "@/lib/api/presence";

export interface AgentControlState {
  toggleAgent: (agentName: string, active: boolean) => Promise<void>;
  isToggling: boolean;
  error: string | null;
  clearError: () => void;
}

export function useAgentControl(
  onSuccess?: (updatedAgent: PresenceDto) => void
): AgentControlState {
  const [isToggling, setIsToggling] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const toggleAgent = useCallback(
    async (agentName: string, active: boolean) => {
      setIsToggling(true);
      setError(null);
      try {
        const updatedAgent = active
          ? await activateAgent(agentName)
          : await deactivateAgent(agentName);
        onSuccess?.(updatedAgent);
      } catch (err) {
        const message =
          err instanceof Error ? err.message : "Failed to toggle agent";
        setError(message);
        throw err; // Re-throw so caller can handle if needed
      } finally {
        setIsToggling(false);
      }
    },
    [onSuccess]
  );

  const clearError = useCallback(() => {
    setError(null);
  }, []);

  return {
    toggleAgent,
    isToggling,
    error,
    clearError,
  };
}
