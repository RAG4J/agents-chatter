"use client";

import { useCallback, useState } from "react";

import { VStack } from "@chakra-ui/react";

import { Message } from "@/lib/types";
import { MessageBubble } from "@/components/MessageBubble";

interface MessageListProps {
  messages: Message[];
}

export function MessageList({ messages }: MessageListProps) {
  const [highlightedParentId, setHighlightedParentId] = useState<string | null>(null);

  const handleHighlightParent = useCallback((parentId: string | null) => {
    setHighlightedParentId(parentId ?? null);
  }, []);

  return (
    <VStack spacing={4} align="stretch" w="100%">
      {messages.map((msg) => (
        <MessageBubble
          key={msg.id}
          message={msg}
          onHighlightParent={handleHighlightParent}
          isParentHighlighted={highlightedParentId === msg.id}
        />
      ))}
    </VStack>
  );
}
