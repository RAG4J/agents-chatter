"use client";

import {
  Alert,
  AlertIcon,
  Box,
  Flex,
  Grid,
  GridItem,
  Skeleton,
  SkeletonText,
  Stack,
  Text,
  useBreakpointValue,
  useColorModeValue
} from "@chakra-ui/react";

import { AgentControlPanel } from "@/components/AgentControlPanel";
import { MessageComposer } from "@/components/MessageComposer";
import { MessageHeader } from "@/components/MessageHeader";
import { MessageList } from "@/components/MessageList";
import { useMessagesFeed } from "@/hooks/useMessagesFeed";
import { useModerationEvents } from "@/hooks/useModerationEvents";
import { usePresence } from "@/hooks/usePresence";

export default function ChatShell() {
  const {
    messages,
    publish,
    status,
    loading,
    error,
    source,
    isSending,
    clearError,
    clearMessages
  } = useMessagesFeed();

  const { participants } = usePresence();
  const {
    events: moderationEvents,
    error: moderationError,
    clearError: clearModerationError,
    clearEvents: clearModerationEvents
  } = useModerationEvents(10);

  const isMobile = useBreakpointValue({ base: true, md: false });
  const messagePanelBg = useColorModeValue("white", "whiteAlpha.100");
  const messagePanelBorder = useColorModeValue("gray.200", "whiteAlpha.200");
  const messagePanelShadow = useColorModeValue("xl", "2xl");
  const insightsBg = useColorModeValue("white", "whiteAlpha.100");
  const insightsBorder = useColorModeValue("gray.200", "whiteAlpha.200");
  const insightsBodyColor = useColorModeValue("gray.600", "gray.400");
  const insightsCaptionColor = useColorModeValue("gray.500", "gray.500");
  const highlightTextColor = useColorModeValue("orange.500", "orange.300");
  const insightsHeadingColor = useColorModeValue("gray.800", "gray.100");
  const recentHeadingColor = useColorModeValue("gray.700", "gray.200");
  const moderationCardBg = useColorModeValue("gray.50", "blackAlpha.500");
  const moderationBorderColor = useColorModeValue("gray.200", "whiteAlpha.200");
  const moderationTextColor = useColorModeValue("gray.700", "gray.100");

  return (
    <Box px={{ base: 4, md: 8, lg: 12 }} py={{ base: 8, md: 12 }}>
      <Stack spacing={8}>
        <MessageHeader
          status={status}
          source={source}
          onClear={async () => {
            await clearMessages();
            await clearModerationEvents();
          }}
        />
        <Grid
          templateColumns={{ base: "1fr", lg: "300px 1fr 400px" }}
          gap={{ base: 6, lg: 6 }}
        >
          {/* Agent Control Sidebar */}
          <GridItem display={{ base: "none", lg: "block" }}>
            <AgentControlPanel participants={participants} />
          </GridItem>
          
          {/* Main Chat Panel */}
          <GridItem>
            <Flex
              direction="column"
              bg={messagePanelBg}
              borderRadius="3xl"
              p={{ base: 4, md: 8 }}
              minH="70vh"
              boxShadow={messagePanelShadow}
              border="1px solid"
              borderColor={messagePanelBorder}
              transition="background-color 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease"
            >
              {error && (
                <Alert status="warning" borderRadius="md" mb={4}>
                  <AlertIcon />
                  {error}
                </Alert>
              )}
              <Box flex="1" overflowY="auto" pr={1}>
                {loading && messages.length === 0 ? (
                  <Stack spacing={4}>
                    <Skeleton height="20px" />
                    <Skeleton height="80px" />
                    <Skeleton height="80px" />
                    <SkeletonText noOfLines={3} spacing="4" />
                  </Stack>
                ) : (
                  <MessageList messages={messages} />
                )}
              </Box>
              <Box mt={6}>
                <MessageComposer
                  onSubmit={publish}
                  isSending={isSending}
                  externalError={error}
                  onDismissError={clearError}
                />
              </Box>
            </Flex>
          </GridItem>
          
          {/* Insights Panel */}
          <GridItem>
            <Stack
              spacing={4}
              bg={insightsBg}
              borderRadius="3xl"
              p={{ base: 4, md: 6 }}
              boxShadow={messagePanelShadow}
              border="1px solid"
              borderColor={insightsBorder}
              transition="background-color 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease"
            >
              <Text fontSize="lg" fontWeight="semibold" color={insightsHeadingColor}>
                Recent Moderation
              </Text>
              {moderationError && (
                <Alert status="warning" variant="subtle" borderRadius="md">
                  <AlertIcon />
                  {moderationError}
                  <Text
                    as="button"
                    ml={2}
                    fontWeight="semibold"
                    textDecoration="underline"
                    onClick={clearModerationError}
                  >
                    Dismiss
                  </Text>
                </Alert>
              )}
              <Stack spacing={3}>
                {moderationEvents.length === 0 ? (
                  <Text fontSize="sm" color={insightsBodyColor}>
                    No moderation actions yet.
                  </Text>
                ) : (
                  moderationEvents.map((event) => (
                      <Box
                        key={`${event.threadId}-${event.occurredAt.getTime()}`}
                      bg={moderationCardBg}
                      borderRadius="lg"
                      p={3}
                      border="1px solid"
                      borderColor={moderationBorderColor}
                      transition="background-color 0.2s ease, border-color 0.2s ease"
                    >
                      <Text fontSize="sm" fontWeight="medium" color={highlightTextColor}>
                        {event.agent}
                      </Text>
                      <Text fontSize="xs" color={insightsCaptionColor}>
                        {event.occurredAt.toLocaleTimeString()} • Thread{" "}
                        {event.threadId.slice(0, 8)}
                      </Text>
                      <Text fontSize="sm" color={moderationTextColor} mt={2}>
                        {event.rationale}
                      </Text>
                      {event.messagePreview && (
                        <Text fontSize="xs" color={insightsCaptionColor} mt={2} fontStyle="italic">
                          “{event.messagePreview}”
                        </Text>
                      )}
                    </Box>
                  ))
                )}
              </Stack>
              {source === "mock" && (
                <Text fontSize="sm" color={highlightTextColor}>
                  You&apos;re currently viewing mock data. Start the backend to
                  see live updates.
                </Text>
              )}
              {isMobile && (
                <Text fontSize="sm" color={insightsBodyColor}>
                  Tip: Rotate your device for a wider canvas or view on desktop
                  to see the split layout.
                </Text>
              )}
              
              {/* Mobile Agent Control */}
              <Box display={{ base: "block", lg: "none" }} mt={4}>
                <AgentControlPanel participants={participants} />
              </Box>
            </Stack>
          </GridItem>
        </Grid>
      </Stack>
    </Box>
  );
}
