export type AuthorType = "agent" | "human";

export interface Author {
  name: string;
  type: AuthorType;
  avatarUrl?: string;
}

export interface Message {
  id: string;
  author: Author;
  content: string;
  timestamp: string;
}
