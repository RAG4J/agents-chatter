"use client";

import {
  Box,
  Flex,
  HStack,
  Text,
  Avatar,
  useColorModeValue
} from "@chakra-ui/react";

import { Message } from "@/lib/types";

interface MessageBubbleProps {
  message: Message;
}

export function MessageBubble({ message }: MessageBubbleProps) {
  const isAgent = message.author.type === "agent";

  const bubbleBg = useColorModeValue(
    isAgent ? "brand.100" : "gray.100",
    isAgent ? "brand.600" : "gray.700"
  );
  const bubbleColor = useColorModeValue("gray.900", "gray.50");

  return (
    <Flex justify={isAgent ? "flex-start" : "flex-end"} w="100%">
      <HStack
        maxW="80%"
        spacing={3}
        align="flex-start"
        flexDir={isAgent ? "row" : "row-reverse"}
      >
        <Avatar
          size="sm"
          name={message.author.name}
          bg={isAgent ? "brand.500" : "gray.500"}
        />
        <Box
          bg={bubbleBg}
          color={bubbleColor}
          px={4}
          py={3}
          borderRadius="2xl"
          boxShadow="md"
        >
          <Text fontSize="sm" fontWeight="semibold" mb={1}>
            {message.author.name}
          </Text>
          <Text whiteSpace="pre-wrap">{message.content}</Text>
          <Text
            fontSize="xs"
            mt={2}
            opacity={0.6}
            textAlign={isAgent ? "left" : "right"}
          >
            {new Date(message.timestamp).toLocaleTimeString([], {
              hour: "2-digit",
              minute: "2-digit"
            })}
          </Text>
        </Box>
      </HStack>
    </Flex>
  );
}
