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
import { IoMoon, IoSunny, IoTrashOutline } from "react-icons/io5";

import { PresenceDto } from "@/lib/api/presence";

interface MessageHeaderProps {
  status: "idle" | "connecting" | "open" | "closed" | "error";
  source: "backend" | "mock";
  participants?: PresenceDto[];
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

export function MessageHeader({ status, source, participants = [], onClear }: MessageHeaderProps) {
  const { toggleColorMode, colorMode } = useColorMode();
  const bg = useColorModeValue("white", "whiteAlpha.200");
  const headingColor = useColorModeValue("gray.800", "gray.100");
  const statusColor = useColorModeValue("gray.600", "gray.400");
  const onlineColor = useColorModeValue("gray.600", "gray.400");
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
        <Text fontSize="sm" color={onlineColor}>
          {participants.filter((participant) => participant.online).length} online
        </Text>
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
