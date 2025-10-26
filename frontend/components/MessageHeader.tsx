"use client";

import {
  Badge,
  Box,
  Flex,
  Heading,
  HStack,
  IconButton,
  Stack,
  Text,
  Tooltip,
  useColorMode,
  useColorModeValue
} from "@chakra-ui/react";
import { IoMoon, IoSunny, IoTrashOutline } from "react-icons/io5";

interface MessageHeaderProps {
  status: "idle" | "connecting" | "open" | "closed" | "error";
  source: "backend" | "mock";
  onClear?: () => void | Promise<void>;
}

const statusConfig: Record<
  MessageHeaderProps["status"],
  { label: string; color: string }
> = {
  idle: { label: "Awaiting activity", color: "gray" },
  connecting: { label: "Connecting…", color: "yellow" },
  open: { label: "Live updates enabled", color: "green" },
  closed: { label: "Realtime offline", color: "orange" },
  error: { label: "Realtime error", color: "red" }
};

export function MessageHeader({ status, source, onClear }: MessageHeaderProps) {
  const { toggleColorMode, colorMode } = useColorMode();
  const bg = useColorModeValue("white", "whiteAlpha.200");
  const headingColor = useColorModeValue("gray.800", "gray.100");
  const statusColor = useColorModeValue("gray.600", "gray.400");
  const borderColor = useColorModeValue("gray.200", "whiteAlpha.200");
  const icon = colorMode === "light" ? <IoMoon /> : <IoSunny />;
  const config = statusConfig[status];

  return (
    <Flex
      bg={bg}
      px={6}
      py={4}
      borderRadius="2xl"
      align="center"
      justify="space-between"
      boxShadow="lg"
      border="1px solid"
      borderColor={borderColor}
      transition="background-color 0.2s ease, border-color 0.2s ease"
    >
      <Stack spacing={1}>
        <Heading size="md" color={headingColor}>
          Agent Collaboration Hub
        </Heading>
        <Text fontSize="sm" color={statusColor}>
          {config.label}
        </Text>
      </Stack>
      <HStack spacing={3}>
        <Badge colorScheme={config.color} variant="subtle">
          {status.toUpperCase()}
        </Badge>
        {source === "mock" && (
          <Badge colorScheme="orange" variant="outline">
            Mock data
          </Badge>
        )}
        {onClear && (
          <Tooltip label="Clear all messages and moderation events">
            <IconButton
              aria-label="Clear all"
              icon={<IoTrashOutline />}
              variant="outline"
              colorScheme="red"
              onClick={onClear}
            />
          </Tooltip>
        )}
        <Box>
          <IconButton
            aria-label="Toggle color mode"
            icon={icon}
            variant="outline"
            colorScheme="gray"
            onClick={toggleColorMode}
          />
        </Box>
      </HStack>
    </Flex>
  );
}
