"use client";

import { FocusEvent } from "react";

import {
  Avatar,
  Box,
  Divider,
  Flex,
  HStack,
  IconButton,
  Stack,
  Text,
  Tooltip,
  useColorModeValue
} from "@chakra-ui/react";
import { FiInfo } from "react-icons/fi";

import { Message } from "@/lib/types";

interface MessageBubbleProps {
  message: Message;
  onHighlightParent: (parentId: string | null) => void;
  isParentHighlighted: boolean;
}

export function MessageBubble({
  message,
  onHighlightParent,
  isParentHighlighted
}: MessageBubbleProps) {
  const isAgent = message.author.type === "agent";

  const bubbleBg = useColorModeValue(
    isAgent ? "brand.100" : "gray.100",
    isAgent ? "brand.600" : "gray.700"
  );
  const bubbleColor = useColorModeValue("gray.900", "gray.50");
  const highlightBorderColor = useColorModeValue("brand.400", "brand.200");
  const highlightGlow = useColorModeValue(
    "rgba(59, 130, 246, 0.35)",
    "rgba(129, 140, 248, 0.45)"
  );
  const focusRingColor = useColorModeValue("blue.300", "blue.500");

  const activateParentHighlight = () =>
    onHighlightParent(message.parentMessageId ?? null);

  const clearParentHighlight = (event: FocusEvent<HTMLDivElement>) => {
    const nextTarget = event.relatedTarget as HTMLElement | null;
    if (!nextTarget || !event.currentTarget.contains(nextTarget)) {
      onHighlightParent(null);
    }
  };

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
          boxShadow={
            isParentHighlighted ? `0 0 0 2px ${highlightGlow}` : "md"
          }
          border="1px solid"
          borderColor={isParentHighlighted ? highlightBorderColor : "transparent"}
          transition="box-shadow 0.2s ease, border-color 0.2s ease"
          tabIndex={0}
          role="group"
          onMouseEnter={activateParentHighlight}
          onMouseLeave={() => onHighlightParent(null)}
          onFocusCapture={activateParentHighlight}
          onBlurCapture={clearParentHighlight}
          _focusVisible={{
            outline: "none",
            boxShadow: `0 0 0 2px ${focusRingColor}`
          }}
        >
          <Flex
            align="center"
            justify={isAgent ? "space-between" : "flex-start"}
            gap={2}
            mb={1}
          >
            <Text fontSize="sm" fontWeight="semibold">
              {message.author.name}
            </Text>
            <Tooltip
              label={
                <Box maxW="240px">
                  <Stack spacing={1} divider={<Divider borderColor="whiteAlpha.300" />}>
                    <MetadataRow label="Message ID" value={message.id} />
                    <MetadataRow label="Thread ID" value={message.threadId} />
                    <MetadataRow
                      label="Parent ID"
                      value={message.parentMessageId ?? "—"}
                    />
                    <MetadataRow
                      label="Origin"
                      value={`${message.originType} • depth ${message.agentReplyDepth}`}
                    />
                    <MetadataRow
                      label="Timestamp"
                      value={new Date(message.timestamp).toLocaleString()}
                    />
                  </Stack>
                </Box>
              }
              hasArrow
              openDelay={200}
              closeDelay={100}
              placement={isAgent ? "right" : "left"}
            >
              <IconButton
                size="xs"
                variant="ghost"
                colorScheme="gray"
                aria-label="Show message metadata"
                icon={<FiInfo />}
              />
            </Tooltip>
          </Flex>
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

interface MetadataRowProps {
  label: string;
  value: string;
}

function MetadataRow({ label, value }: MetadataRowProps) {
  return (
    <Box>
      <Text fontSize="xs" fontWeight="bold" textTransform="uppercase" opacity={0.7}>
        {label}
      </Text>
      <Text fontSize="xs" wordBreak="break-word">
        {value}
      </Text>
    </Box>
  );
}
