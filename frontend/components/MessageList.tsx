"use client";

import { VStack } from "@chakra-ui/react";

import { Message } from "@/lib/types";
import { MessageBubble } from "@/components/MessageBubble";

interface MessageListProps {
  messages: Message[];
}

export function MessageList({ messages }: MessageListProps) {
  return (
    <VStack spacing={4} align="stretch" w="100%">
      {messages.map((msg) => (
        <MessageBubble key={msg.id} message={msg} />
      ))}
    </VStack>
  );
}
