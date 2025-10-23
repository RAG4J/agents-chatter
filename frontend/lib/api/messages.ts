import { Message } from "@/lib/types";
import { seedMessages } from "@/lib/mockMessages";

export type MessageDto = {
  id: string;
  author: string;
  payload: string;
  timestamp: string;
};

const DEFAULT_API_BASE = "http://localhost:8080/api";
const API_BASE =
  (process.env.NEXT_PUBLIC_API_BASE &&
    process.env.NEXT_PUBLIC_API_BASE.replace(/\/$/, "")) ??
  DEFAULT_API_BASE;

const hasBackend =
  typeof process !== "undefined" && !!process.env.NEXT_PUBLIC_API_BASE;

export function mapDtoToMessage(dto: MessageDto): Message {
  return {
    id: dto.id,
    author: {
      name: dto.author,
      type: dto.author.toLowerCase().includes("agent") ? "agent" : "human"
    },
    content: dto.payload,
    timestamp: dto.timestamp
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
    return {
      data: {
        id: `mock-${Date.now()}`,
        author: message.author,
        content: message.content,
        timestamp: new Date().toISOString()
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
