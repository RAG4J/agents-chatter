import { render, screen } from "@testing-library/react";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import ChatShell from "@/components/ChatShell";

describe("ChatShell", () => {
  beforeEach(() => {
    vi.stubGlobal(
      "EventSource",
      class {
        constructor() {
          return this;
        }
        close() {}
        onmessage = null;
        onerror = null;
        onopen = null;
        addEventListener() {}
        removeEventListener() {}
        dispatchEvent() {
          return true;
        }
        readyState = 1;
        url = "";
        withCredentials = false;
      }
    );
  });

  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it("renders seeded messages", () => {
    render(<ChatShell />);
    expect(
      screen.getByText(/Agent Collaboration Hub/i)
    ).toBeInTheDocument();
    expect(screen.getByText(/Atlas Agent/)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/Type a message/i)).toBeInTheDocument();
    expect(screen.getByText(/IDLE/i)).toBeInTheDocument();
    expect(screen.getByText(/Mock data/i)).toBeInTheDocument();
    expect(screen.getByText(/Recent moderation/i)).toBeInTheDocument();
  });
});
