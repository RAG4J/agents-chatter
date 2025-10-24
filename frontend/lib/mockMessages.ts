import { Message } from "@/lib/types";

export const seedMessages: Message[] = [
  {
    id: "m-001",
    author: { name: "Atlas Agent", type: "agent" },
    content: "Good morning! I've reviewed the overnight log—no critical incidents.",
    timestamp: "2025-10-23T07:35:00Z",
    threadId: "m-001",
    parentMessageId: null,
    originType: "AGENT",
    agentReplyDepth: 1
  },
  {
    id: "m-002",
    author: { name: "You", type: "human" },
    content: "Thanks! Can you summarise the open tasks for today?",
    timestamp: "2025-10-23T07:36:15Z",
    threadId: "m-002",
    parentMessageId: "m-001",
    originType: "HUMAN",
    agentReplyDepth: 0
  },
  {
    id: "m-003",
    author: { name: "Atlas Agent", type: "agent" },
    content: "Sure thing. 1) Prepare release notes draft, 2) Finalise chat UI mockups, 3) Sync with infra on rollout plan.",
    timestamp: "2025-10-23T07:36:45Z",
    threadId: "m-001",
    parentMessageId: "m-002",
    originType: "AGENT",
    agentReplyDepth: 1
  },
  {
    id: "m-004",
    author: { name: "Design Agent", type: "agent" },
    content: "I uploaded the latest mockups to the shared drive—feel free to leave comments.",
    timestamp: "2025-10-23T07:38:12Z",
    threadId: "m-004",
    parentMessageId: "m-002",
    originType: "AGENT",
    agentReplyDepth: 1
  }
];
