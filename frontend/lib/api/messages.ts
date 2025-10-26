import { Message, MessageOrigin } from "@/lib/types";
import { seedMessages } from "@/lib/mockMessages";

export type MessageDto = {
  id: string;
  author: string;
  payload: string;
  timestamp: string;
  threadId?: string;
  parentMessageId?: string | null;
  originType?: MessageOrigin;
  agentReplyDepth?: number;
};

const DEFAULT_API_BASE = "http://localhost:8080/api";
const API_BASE =
  (process.env.NEXT_PUBLIC_API_BASE &&
    process.env.NEXT_PUBLIC_API_BASE.replace(/\/$/, "")) ??
  DEFAULT_API_BASE;

const hasBackend =
  typeof process !== "undefined" && !!process.env.NEXT_PUBLIC_API_BASE;

export function mapDtoToMessage(dto: MessageDto): Message {
  const origin: MessageOrigin =
    dto.originType === "AGENT" || dto.originType === "HUMAN"
      ? dto.originType
      : "UNKNOWN";
  const inferredAuthorType: Message["author"]["type"] =
    origin === "AGENT"
      ? "agent"
      : origin === "HUMAN"
        ? "human"
        : dto.author.toLowerCase().includes("agent")
          ? "agent"
          : "human";
  return {
    id: dto.id,
    author: {
      name: dto.author,
      type: inferredAuthorType
    },
    content: dto.payload,
    timestamp: dto.timestamp,
    threadId: dto.threadId ?? dto.id,
    parentMessageId:
      dto.parentMessageId === undefined ? null : dto.parentMessageId,
    originType: origin,
    agentReplyDepth: dto.agentReplyDepth ?? 0
  };
}

export async function listMessages(): Promise<{
  data: Message[];
  source: "backend" | "mock";
}> {
  try {
    const res = await fetch(`${API_BASE}/messages`, {
      cache: "no-store"
    });
    if (!res.ok) {
      throw new Error(`Failed to fetch messages: ${res.status} ${res.statusText}`);
    }
    const body = (await res.json()) as MessageDto[];
    return {
      data: body.map(mapDtoToMessage),
      source: "backend"
    };
  } catch (error) {
    if (hasBackend) {
      console.error("Falling back to mock messages:", error);
    }
    return {
      data: seedMessages,
      source: "mock"
    };
  }
}

export async function postMessage(
  message: Pick<Message, "author" | "content">
): Promise<{ data: Message; source: "backend" | "mock" }> {
  try {
    const res = await fetch(`${API_BASE}/messages`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        author: message.author.name,
        payload: message.content
      })
    });
    if (!res.ok) {
      throw new Error(`Failed to post message: ${res.status} ${res.statusText}`);
    }
    const body = (await res.json()) as MessageDto;
    return { data: mapDtoToMessage(body), source: "backend" };
  } catch (error) {
    if (hasBackend) {
      console.error("Falling back to mock echo:", error);
    }
    const now = Date.now();
    const identifier = `mock-${now}`;
    return {
      data: {
        id: identifier,
        author: message.author,
        content: message.content,
        timestamp: new Date(now).toISOString(),
        threadId: identifier,
        parentMessageId: null,
        originType: message.author.type === "agent" ? "AGENT" : "HUMAN",
        agentReplyDepth: message.author.type === "agent" ? 1 : 0
      },
      source: "mock"
    };
  }
}

export function getApiBase(): string {
  return API_BASE;
}

export function resolveWebSocketUrl(locationOrigin?: string): string | null {
  const configured = process.env.NEXT_PUBLIC_WS_URL;
  if (configured && configured.trim().length > 0) {
    return configured;
  }

  const deriveUrl = (base: string): string | null => {
    try {
      const url = new URL(base, locationOrigin ?? undefined);
      const normalizedPath = url.pathname.replace(/\/api\/?$/, "/ws/messages");
      url.pathname = normalizedPath.endsWith("/ws/messages")
        ? normalizedPath
        : `${normalizedPath.replace(/\/$/, "")}/ws/messages`;
      url.protocol = url.protocol === "https:" ? "wss:" : "ws:";
      return url.toString();
    } catch (error) {
      console.warn("Unable to derive WebSocket URL from base:", error);
      return null;
    }
  };

  return deriveUrl(API_BASE);
}

export async function clearMessages(): Promise<void> {
  try {
    const res = await fetch(`${API_BASE}/messages`, {
      method: "DELETE"
    });
    if (!res.ok) {
      throw new Error(`Failed to clear messages: ${res.status} ${res.statusText}`);
    }
  } catch (error) {
    if (hasBackend) {
      console.error("Failed to clear messages:", error);
      throw error;
    }
    // In mock mode, silently succeed
  }
}
