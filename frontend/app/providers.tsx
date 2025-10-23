"use client";

import { ChakraProvider, ColorModeScript } from "@chakra-ui/react";
import { PropsWithChildren } from "react";

import theme from "@/lib/theme";

export default function Providers({ children }: PropsWithChildren) {
  return (
    <ChakraProvider theme={theme}>
      <ColorModeScript initialColorMode={theme.config.initialColorMode} />
      {children}
    </ChakraProvider>
  );
}
