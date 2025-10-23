"use client";

import {
  Avatar,
  AvatarGroup,
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
import { IoMoon, IoSunny } from "react-icons/io5";

import { PresenceDto } from "@/lib/api/presence";

interface MessageHeaderProps {
  status: "idle" | "connecting" | "open" | "closed" | "error";
  source: "backend" | "mock";
  participants?: PresenceDto[];
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

export function MessageHeader({ status, source, participants = [] }: MessageHeaderProps) {
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
        <AvatarGroup size="sm" max={5}>
          {participants
            .filter((participant) => participant.online)
            .map((participant) => (
              <Tooltip key={participant.name} label={`${participant.name} (${participant.role.toLowerCase()})`}>
                <Avatar
                  name={participant.name}
                  size="sm"
                  bg={participant.role === "AGENT" ? "brand.500" : "gray.500"}
                />
              </Tooltip>
            ))}
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
      <HStack spacing={3}>
        <Text fontSize="sm" color="gray.400">
          {participants.filter((participant) => participant.online).length} online
        </Text>
        <Box>
          <IconButton
            aria-label="Toggle color mode"
            icon={icon}
            variant="ghost"
            onClick={toggleColorMode}
          />
        </Box>
      </HStack>
    </Flex>
  );
}
