export type AuthorType = "agent" | "human";

export interface Author {
  name: string;
  type: AuthorType;
  avatarUrl?: string;
}

export type MessageOrigin = "HUMAN" | "AGENT" | "UNKNOWN";

export interface Message {
  id: string;
  author: Author;
  content: string;
  timestamp: string;
  threadId: string;
  parentMessageId?: string | null;
  originType: MessageOrigin;
  agentReplyDepth: number;
}
