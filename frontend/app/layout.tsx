import type { Metadata } from "next";
import { ReactNode } from "react";

import Providers from "./providers";
import "./globals.css";

export const metadata: Metadata = {
  title: "Agents Chatter",
  description: "Chat with AI agents and teammates in a single interface."
};

export default function RootLayout({ children }: { children: ReactNode }) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body suppressHydrationWarning>
        <Providers>{children}</Providers>
      </body>
    </html>
  );
}
