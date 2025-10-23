"use client";

import {
  AvatarGroup,
  Avatar,
  Badge,
  Box,
  Flex,
  Heading,
  IconButton,
  Stack,
  Text,
  useColorMode,
  useColorModeValue
} from "@chakra-ui/react";
import { IoMoon, IoSunny } from "react-icons/io5";

interface MessageHeaderProps {
  status: "idle" | "connecting" | "open" | "closed" | "error";
  source: "backend" | "mock";
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

export function MessageHeader({ status, source }: MessageHeaderProps) {
  const { toggleColorMode, colorMode } = useColorMode();
  const bg = useColorModeValue("whiteAlpha.900", "whiteAlpha.200");
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
    >
      <Stack spacing={1}>
        <Heading size="md">Agent Collaboration Hub</Heading>
        <Text fontSize="sm" color="gray.500">
          {config.label}
        </Text>
      </Stack>
      <Stack spacing={2} align="center">
        <AvatarGroup size="sm" max={3}>
          <Avatar name="Atlas Agent" />
          <Avatar name="Design Agent" />
          <Avatar name="You" />
        </AvatarGroup>
        <Badge colorScheme={config.color} variant="subtle">
          {status.toUpperCase()}
        </Badge>
        {source === "mock" && (
          <Badge colorScheme="orange" variant="outline">
            Mock data
          </Badge>
        )}
      </Stack>
      <Box>
        <IconButton
          aria-label="Toggle color mode"
          icon={icon}
          variant="ghost"
          onClick={toggleColorMode}
        />
      </Box>
    </Flex>
  );
}
