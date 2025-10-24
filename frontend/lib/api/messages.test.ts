import { describe, expect, it } from "vitest";

import { mapDtoToMessage, type MessageDto } from "./messages";

describe("mapDtoToMessage", () => {
  it("maps backend metadata fields to the UI model", () => {
    const dto: MessageDto = {
      id: "abc-123",
      author: "EchoAgent",
      payload: "echo hello",
      timestamp: "2025-10-24T10:00:00Z",
      threadId: "thread-1",
      parentMessageId: "root-1",
      originType: "AGENT",
      agentReplyDepth: 2
    };

    const message = mapDtoToMessage(dto);

    expect(message.threadId).toBe("thread-1");
    expect(message.parentMessageId).toBe("root-1");
    expect(message.originType).toBe("AGENT");
    expect(message.agentReplyDepth).toBe(2);
    expect(message.author.type).toBe("agent");
  });

  it("falls back to sensible defaults when metadata missing", () => {
    const dto: MessageDto = {
      id: "def-456",
      author: "You",
      payload: "Hello there!",
      timestamp: "2025-10-24T10:01:00Z"
    };

    const message = mapDtoToMessage(dto);

    expect(message.threadId).toBe("def-456");
    expect(message.parentMessageId).toBeNull();
    expect(message.originType).toBe("UNKNOWN");
    expect(message.agentReplyDepth).toBe(0);
    expect(message.author.type).toBe("human");
  });
});
