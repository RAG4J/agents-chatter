"use client";

import {
  Alert,
  AlertIcon,
  Box,
  Container,
  Flex,
  Grid,
  GridItem,
  Skeleton,
  SkeletonText,
  Stack,
  Text,
  useBreakpointValue
} from "@chakra-ui/react";

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
    clearError
  } = useMessagesFeed();

  const { participants } = usePresence();
  const {
    events: moderationEvents,
    error: moderationError,
    clearError: clearModerationError
  } = useModerationEvents(10);

  const isMobile = useBreakpointValue({ base: true, md: false });

  return (
    <Container maxW="6xl" py={{ base: 8, md: 12 }}>
      <Stack spacing={8}>
        <MessageHeader
          status={status}
          source={source}
          participants={participants}
        />
        <Grid
          templateColumns={{ base: "1fr", lg: "3fr 2fr" }}
          gap={{ base: 6, lg: 10 }}
        >
          <GridItem>
            <Flex
              direction="column"
              bg="blackAlpha.400"
              borderRadius="3xl"
              p={{ base: 4, md: 8 }}
              minH="70vh"
              backdropFilter="blur(12px)"
              boxShadow="2xl"
              border="1px solid"
              borderColor="whiteAlpha.200"
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
          <GridItem>
            <Stack
              spacing={4}
              bg="whiteAlpha.100"
              borderRadius="3xl"
              p={{ base: 4, md: 6 }}
              boxShadow="xl"
              border="1px solid"
              borderColor="whiteAlpha.200"
            >
              <Text fontSize="lg" fontWeight="semibold">
                Conversation Insights
              </Text>
              <Text fontSize="sm" color="gray.400">
                Preview how agents collaborate in real time. This panel can
                surface summaries, suggested prompts, or system status once the
                backend services are connected.
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
                <Text fontSize="sm" fontWeight="semibold" color="gray.200">
                  Recent moderation
                </Text>
                {moderationEvents.length === 0 ? (
                  <Text fontSize="sm" color="gray.500">
                    No moderation actions yet.
                  </Text>
                ) : (
                  moderationEvents.map((event) => (
                    <Box
                      key={`${event.threadId}-${event.occurredAt.getTime()}`}
                      bg="blackAlpha.500"
                      borderRadius="lg"
                      p={3}
                      border="1px solid"
                      borderColor="whiteAlpha.200"
                    >
                      <Text fontSize="sm" fontWeight="medium" color="orange.200">
                        {event.agent}
                      </Text>
                      <Text fontSize="xs" color="gray.400">
                        {event.occurredAt.toLocaleTimeString()} • Thread{" "}
                        {event.threadId.slice(0, 8)}
                      </Text>
                      <Text fontSize="sm" color="gray.100" mt={2}>
                        {event.rationale}
                      </Text>
                      {event.messagePreview && (
                        <Text fontSize="xs" color="gray.400" mt={2} fontStyle="italic">
                          “{event.messagePreview}”
                        </Text>
                      )}
                    </Box>
                  ))
                )}
              </Stack>
              {source === "mock" && (
                <Text fontSize="sm" color="orange.300">
                  You&apos;re currently viewing mock data. Start the backend to
                  see live updates.
                </Text>
              )}
              {isMobile && (
                <Text fontSize="sm" color="gray.500">
                  Tip: Rotate your device for a wider canvas or view on desktop
                  to see the split layout.
                </Text>
              )}
            </Stack>
          </GridItem>
        </Grid>
      </Stack>
    </Container>
  );
}
