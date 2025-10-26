"use client";

import { extendTheme, StyleFunctionProps, ThemeConfig } from "@chakra-ui/react";
import { mode } from "@chakra-ui/theme-tools";

const config: ThemeConfig = {
  initialColorMode: "dark",
  useSystemColorMode: false
};

const colors = {
  brand: {
    50: "#f2f6ff",
    100: "#dce6ff",
    200: "#b7ccff",
    300: "#8badff",
    400: "#5a8aff",
    500: "#376cf5",
    600: "#2653d1",
    700: "#1d3fab",
    800: "#172f80",
    900: "#101f57"
  }
};

const theme = extendTheme({
  config,
  colors,
  fonts: {
    heading: "Inter, system-ui, sans-serif",
    body: "Inter, system-ui, sans-serif"
  },
  styles: {
    global: (props: StyleFunctionProps) => ({
      body: {
        bg: mode("gray.50", "gray.900")(props),
        color: mode("gray.900", "gray.50")(props),
        transition: "background-color 0.2s ease, color 0.2s ease"
      }
    })
  },
  components: {
    Button: {
      defaultProps: {
        colorScheme: "brand"
      }
    }
  }
});

export default theme;
