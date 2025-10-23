import { render, screen } from "@testing-library/react";

import ChatShell from "@/components/ChatShell";

describe("ChatShell", () => {
  it("renders seeded messages", () => {
    render(<ChatShell />);
    expect(
      screen.getByText(/Agent Collaboration Hub/i)
    ).toBeInTheDocument();
    expect(screen.getByText(/Atlas Agent/)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/Type a message/i)).toBeInTheDocument();
    expect(screen.getByText(/IDLE/i)).toBeInTheDocument();
    expect(screen.getByText(/Mock data/i)).toBeInTheDocument();
  });
});
