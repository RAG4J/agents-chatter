export type PresenceRole = "AGENT" | "HUMAN" | "SYSTEM";

export interface PresenceDto {
  name: string;
  role: PresenceRole;
  online: boolean;
  connections: number;
}

const API_BASE =
  process.env.NEXT_PUBLIC_API_BASE?.replace(/\/$/, "") ?? "http://localhost:8080/api";
const PRESENCE_ENDPOINT = `${API_BASE}/presence`;

export async function fetchPresence(): Promise<PresenceDto[]> {
  try {
    const response = await fetch(PRESENCE_ENDPOINT, { cache: "no-store" });
    if (!response.ok) {
      throw new Error(`Failed to fetch presence: ${response.statusText}`);
    }
    return (await response.json()) as PresenceDto[];
  } catch (error) {
    console.warn("Presence fetch failed, defaulting to empty list:", error);
    return [];
  }
}

export function subscribePresence(
  onMessage: (presence: PresenceDto[]) => void,
  onError?: (error: Event) => void
): () => void {
  const source = new EventSource(`${PRESENCE_ENDPOINT}/stream`);
  source.onmessage = (event) => {
    try {
      const data = JSON.parse(event.data) as PresenceDto[];
      onMessage(data);
    } catch (error) {
      console.warn("Unable to parse presence event stream payload", error);
    }
  };
  source.onerror = (event) => {
    console.warn("Presence SSE error", event);
    onError?.(event);
  };
  return () => {
    source.close();
  };
}
