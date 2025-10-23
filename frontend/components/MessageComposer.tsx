"use client";

import {
  Alert,
  AlertIcon,
  Box,
  Button,
  Flex,
  HStack,
  Textarea,
  FormControl,
  FormErrorMessage
} from "@chakra-ui/react";
import { ChangeEvent, FormEvent, useState } from "react";
import { IoSend, IoTrash } from "react-icons/io5";

import { Message } from "@/lib/types";

interface MessageComposerProps {
  onSubmit: (draft: { author: Message["author"]; content: string }) => Promise<void> | void;
  isSending?: boolean;
  externalError?: string | null;
  onDismissError?: () => void;
}

export function MessageComposer({
  onSubmit,
  isSending = false,
  externalError,
  onDismissError
}: MessageComposerProps) {
  const [value, setValue] = useState("");
  const [localError, setLocalError] = useState<string | null>(null);

  const combinedError = externalError ?? localError;

  const handleChange = (
    event: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    setValue(event.target.value);
    if (localError) {
      setLocalError(null);
    }
    if (externalError && onDismissError) {
      onDismissError();
    }
  };

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    if (!value.trim()) {
      setLocalError("Enter a message before sending.");
      return;
    }

    try {
      await onSubmit({
        author: { name: "You", type: "human" },
        content: value
      });
      setValue("");
      setLocalError(null);
      onDismissError?.();
    } catch (err) {
      const message =
        err instanceof Error ? err.message : "Failed to send message.";
      setLocalError(message);
    }
  };

  return (
    <Box as="form" onSubmit={handleSubmit}>
      <FormControl isInvalid={Boolean(combinedError)}>
        <HStack spacing={4} align="flex-start">
          <Textarea
            value={value}
            onChange={handleChange}
            placeholder="Type a message..."
            minH="80px"
            resize="vertical"
            focusBorderColor="brand.400"
            isDisabled={isSending}
          />
          <Flex direction="column" gap={2}>
            <Button
              type="submit"
              rightIcon={<IoSend />}
              isDisabled={!value.trim()}
              isLoading={isSending}
            >
              Send
            </Button>
            <Button
              aria-label="Clear message"
              variant="ghost"
              leftIcon={<IoTrash />}
              onClick={() => {
                setValue("");
                setLocalError(null);
                onDismissError?.();
              }}
              isDisabled={isSending}
            >
              Clear
            </Button>
          </Flex>
        </HStack>
        <FormErrorMessage>{combinedError}</FormErrorMessage>
      </FormControl>
      {externalError && !localError && (
        <Alert status="warning" mt={4} borderRadius="md">
          <AlertIcon />
          {externalError}
        </Alert>
      )}
    </Box>
  );
}
