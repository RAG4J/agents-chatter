"use client";

import {
  Alert,
  AlertIcon,
  Box,
  Divider,
  Flex,
  Heading,
  HStack,
  IconButton,
  Stack,
  Switch,
  Text,
  Tooltip,
  useColorModeValue,
} from "@chakra-ui/react";
import { IoInformationCircleOutline } from "react-icons/io5";

import { useAgentControl } from "@/hooks/useAgentControl";
import type { PresenceDto } from "@/lib/api/presence";

interface AgentControlPanelProps {
  participants: PresenceDto[];
}

export function AgentControlPanel({ participants }: AgentControlPanelProps) {
  const { toggleAgent, isToggling, error, clearError } = useAgentControl();

  const bg = useColorModeValue("white", "whiteAlpha.100");
  const borderColor = useColorModeValue("gray.200", "whiteAlpha.200");
  const headingColor = useColorModeValue("gray.800", "gray.100");
  const sectionHeadingColor = useColorModeValue("gray.600", "gray.400");
  const agentNameColor = useColorModeValue("gray.700", "gray.200");
  const infoColor = useColorModeValue("gray.500", "gray.500");
  const dividerColor = useColorModeValue("gray.200", "whiteAlpha.300");
  const hoverBg = useColorModeValue("gray.50", "whiteAlpha.50");

  const agents = participants.filter((p) => p.role === "AGENT");
  const humans = participants.filter((p) => p.role === "HUMAN");

  const activeAgents = agents.filter((a) => a.active);
  const inactiveAgents = agents.filter((a) => !a.active);

  const getStatusColor = (participant: PresenceDto) => {
    if (!participant.active) return "gray.400";
    if (participant.online) return "green.500";
    return "red.500";
  };

  const getStatusLabel = (participant: PresenceDto) => {
    if (!participant.active) return "Inactive";
    if (participant.online) return "Active & Online";
    return "Active but Offline";
  };

  const handleToggle = async (agentName: string, currentActive: boolean) => {
    try {
      await toggleAgent(agentName, !currentActive);
    } catch (err) {
      // Error is already set in the hook
      console.error("Failed to toggle agent:", err);
    }
  };

  return (
    <Box
      bg={bg}
      borderRadius="3xl"
      p={6}
      boxShadow="xl"
      border="1px solid"
      borderColor={borderColor}
      transition="background-color 0.2s ease, border-color 0.2s ease"
      h="full"
    >
      <Stack spacing={4} h="full">
        <Flex align="center" justify="space-between">
          <Heading size="md" color={headingColor}>
            Agent Control
          </Heading>
          <Tooltip label="Toggle agents to activate or deactivate their participation in conversations">
            <IconButton
              aria-label="Info"
              icon={<IoInformationCircleOutline />}
              size="sm"
              variant="ghost"
              colorScheme="gray"
            />
          </Tooltip>
        </Flex>

        {error && (
          <Alert status="error" borderRadius="md" fontSize="sm">
            <AlertIcon />
            <Box flex="1">{error}</Box>
            <Text
              as="button"
              fontWeight="semibold"
              textDecoration="underline"
              onClick={clearError}
              ml={2}
            >
              Dismiss
            </Text>
          </Alert>
        )}

        <Divider borderColor={dividerColor} />

        {/* Active Agents Section */}
        <Stack spacing={3} flex="1" overflowY="auto">
          <Text
            fontSize="sm"
            fontWeight="semibold"
            color={sectionHeadingColor}
            textTransform="uppercase"
            letterSpacing="wide"
          >
            Active Agents ({activeAgents.length})
          </Text>
          {activeAgents.length === 0 ? (
            <Text fontSize="sm" color={infoColor} fontStyle="italic">
              No active agents
            </Text>
          ) : (
            activeAgents.map((agent) => (
              <Flex
                key={agent.name}
                align="center"
                justify="space-between"
                p={3}
                borderRadius="lg"
                transition="background-color 0.2s"
                _hover={{ bg: hoverBg }}
              >
                <HStack spacing={3} flex="1">
                  <Box
                    w={3}
                    h={3}
                    borderRadius="full"
                    bg={getStatusColor(agent)}
                    flexShrink={0}
                  />
                  <Box flex="1" minW={0}>
                    <Text
                      fontSize="sm"
                      fontWeight="medium"
                      color={agentNameColor}
                      noOfLines={1}
                    >
                      {agent.name}
                    </Text>
                    <Text fontSize="xs" color={infoColor}>
                      {getStatusLabel(agent)}
                    </Text>
                  </Box>
                </HStack>
                <Switch
                  size="md"
                  colorScheme="green"
                  isChecked={agent.active}
                  onChange={() => handleToggle(agent.name, agent.active)}
                  isDisabled={isToggling}
                />
              </Flex>
            ))
          )}

          {/* Inactive Agents Section */}
          {inactiveAgents.length > 0 && (
            <>
              <Divider borderColor={dividerColor} mt={2} />
              <Text
                fontSize="sm"
                fontWeight="semibold"
                color={sectionHeadingColor}
                textTransform="uppercase"
                letterSpacing="wide"
              >
                Inactive Agents ({inactiveAgents.length})
              </Text>
              {inactiveAgents.map((agent) => (
                <Flex
                  key={agent.name}
                  align="center"
                  justify="space-between"
                  p={3}
                  borderRadius="lg"
                  transition="background-color 0.2s"
                  _hover={{ bg: hoverBg }}
                  opacity={0.7}
                >
                  <HStack spacing={3} flex="1">
                    <Box
                      w={3}
                      h={3}
                      borderRadius="full"
                      bg={getStatusColor(agent)}
                      flexShrink={0}
                    />
                    <Box flex="1" minW={0}>
                      <Text
                        fontSize="sm"
                        fontWeight="medium"
                        color={agentNameColor}
                        noOfLines={1}
                      >
                        {agent.name}
                      </Text>
                      <Text fontSize="xs" color={infoColor}>
                        {getStatusLabel(agent)}
                      </Text>
                    </Box>
                  </HStack>
                  <Switch
                    size="md"
                    colorScheme="green"
                    isChecked={agent.active}
                    onChange={() => handleToggle(agent.name, agent.active)}
                    isDisabled={isToggling}
                  />
                </Flex>
              ))}
            </>
          )}

          {/* Humans Section */}
          {humans.length > 0 && (
            <>
              <Divider borderColor={dividerColor} mt={2} />
              <Text
                fontSize="sm"
                fontWeight="semibold"
                color={sectionHeadingColor}
                textTransform="uppercase"
                letterSpacing="wide"
              >
                Humans ({humans.length})
              </Text>
              {humans.map((human) => (
                <Flex key={human.name} align="center" p={3} opacity={0.8}>
                  <HStack spacing={3}>
                    <Box
                      w={3}
                      h={3}
                      borderRadius="full"
                      bg={human.online ? "green.500" : "gray.400"}
                      flexShrink={0}
                    />
                    <Box>
                      <Text
                        fontSize="sm"
                        fontWeight="medium"
                        color={agentNameColor}
                      >
                        {human.name}
                      </Text>
                      <Text fontSize="xs" color={infoColor}>
                        {human.online ? "Online" : "Offline"}
                      </Text>
                    </Box>
                  </HStack>
                </Flex>
              ))}
            </>
          )}
        </Stack>

        {/* Summary */}
        <Box pt={3} borderTop="1px solid" borderColor={dividerColor}>
          <Text fontSize="xs" color={infoColor} textAlign="center">
            {agents.length} agents • {activeAgents.length} active •{" "}
            {inactiveAgents.length} inactive
          </Text>
        </Box>
      </Stack>
    </Box>
  );
}
