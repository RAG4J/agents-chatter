"use client";

import { useEffect, useMemo, useRef, useState } from "react";

import {
  listMessages,
  postMessage,
  resolveWebSocketUrl,
  mapDtoToMessage,
  clearMessages as clearMessagesApi,
  type MessageDto
} from "@/lib/api/messages";
import { Message } from "@/lib/types";

type MessageDraft = {
  author: Message["author"];
  content: string;
};

type ConnectionStatus = "idle" | "connecting" | "open" | "closed" | "error";

const hasConfiguredBackend =
  typeof process !== "undefined" && !!process.env.NEXT_PUBLIC_API_BASE;

export function useMessagesFeed(initial: Message[] = []) {
  const [messages, setMessages] = useState<Message[]>(initial);
  const [status, setStatus] = useState<ConnectionStatus>("idle");
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [source, setSource] = useState<"backend" | "mock">("mock");
  const [isSending, setIsSending] = useState<boolean>(false);
  const reconnectTimer = useRef<NodeJS.Timeout | null>(null);

  useEffect(() => {
    let cancelled = false;

    async function hydrate() {
      setLoading(true);
      setError(null);
      try {
        const result = await listMessages();
        if (cancelled) return;
        setMessages(result.data);
        setSource(result.source);
        if (result.source === "mock" && hasConfiguredBackend) {
          setError(
            "Backend unreachable, showing mock messages. Ensure the Spring Boot service is running."
          );
        }
      } catch (fetchError) {
        if (cancelled) return;
        console.error(fetchError);
        setError(
          fetchError instanceof Error
            ? fetchError.message
            : "Unable to load messages."
        );
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    }

    hydrate();
    return () => {
      cancelled = true;
    };
  }, []);

  useEffect(() => {
    let cancelled = false;
    let socket: WebSocket | null = null;
    let retries = 0;

    if (typeof window === "undefined" || typeof WebSocket === "undefined") {
      return;
    }

    const scheduleReconnect = () => {
      if (reconnectTimer.current) {
        clearTimeout(reconnectTimer.current);
      }
      const timeout = Math.min(1000 * 2 ** retries, 10000);
      reconnectTimer.current = setTimeout(() => {
        retries += 1;
        connect();
      }, timeout);
    };

    const connect = () => {
      if (cancelled) return;
      const url = resolveWebSocketUrl(
        typeof window !== "undefined" ? window.location.origin : undefined
      );
      if (!url) {
        return;
      }

      setStatus("connecting");
      socket = new WebSocket(url);

      socket.addEventListener("open", () => {
        if (cancelled) return;
        retries = 0;
        setStatus("open");
      });

      socket.addEventListener("message", (event) => {
        try {
          const payload = JSON.parse(event.data) as MessageDto;
          const message = mapDtoToMessage(payload);
          setMessages((prev) => {
            const exists = prev.some((entry) => entry.id === message.id);
            return exists ? prev : [...prev, message];
          });
          setSource("backend");
        } catch (parseError) {
          console.warn("Failed to parse message payload", parseError);
        }
      });

      socket.addEventListener("close", () => {
        if (cancelled) return;
        setStatus("closed");
        scheduleReconnect();
      });

      socket.addEventListener("error", (wsError) => {
        if (cancelled) return;
        console.error("WebSocket error", wsError);
        setStatus("error");
        socket?.close();
      });
    };

    connect();

    return () => {
      cancelled = true;
      if (reconnectTimer.current) {
        clearTimeout(reconnectTimer.current);
      }
      socket?.close();
    };
  }, []);

  const publish = useMemo(
    () => async ({ author, content }: MessageDraft) => {
      if (!content.trim()) {
        return;
      }
      setIsSending(true);
      setError(null);
      try {
        const result = await postMessage({ author, content });
        setMessages((prev) => {
          const exists = prev.some((entry) => entry.id === result.data.id);
          return exists ? prev : [...prev, result.data];
        });
        setSource(result.source);
        if (result.source === "mock" && hasConfiguredBackend) {
          setError(
            "Message sent in mock mode because the backend is unavailable."
          );
        }
      } catch (sendError) {
        setError(
          sendError instanceof Error
            ? sendError.message
            : "Failed to send message."
        );
      } finally {
        setIsSending(false);
      }
    },
    []
  );

  const clearMessages = useMemo(
    () => async () => {
      setError(null);
      try {
        await clearMessagesApi();
        setMessages([]);
      } catch (clearError) {
        setError(
          clearError instanceof Error
            ? clearError.message
            : "Failed to clear messages."
        );
      }
    },
    []
  );

  return {
    messages,
    status,
    loading,
    error,
    source,
    isSending,
    publish,
    clearMessages,
    clearError: () => setError(null)
  };
}
