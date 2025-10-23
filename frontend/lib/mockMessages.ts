import { Message } from "@/lib/types";

export const seedMessages: Message[] = [
  {
    id: "m-001",
    author: { name: "Atlas Agent", type: "agent" },
    content: "Good morning! I've reviewed the overnight log—no critical incidents.",
    timestamp: "2025-10-23T07:35:00Z"
  },
  {
    id: "m-002",
    author: { name: "You", type: "human" },
    content: "Thanks! Can you summarise the open tasks for today?",
    timestamp: "2025-10-23T07:36:15Z"
  },
  {
    id: "m-003",
    author: { name: "Atlas Agent", type: "agent" },
    content: "Sure thing. 1) Prepare release notes draft, 2) Finalise chat UI mockups, 3) Sync with infra on rollout plan.",
    timestamp: "2025-10-23T07:36:45Z"
  },
  {
    id: "m-004",
    author: { name: "Design Agent", type: "agent" },
    content: "I uploaded the latest mockups to the shared drive—feel free to leave comments.",
    timestamp: "2025-10-23T07:38:12Z"
  }
];
