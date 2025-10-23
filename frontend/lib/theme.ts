"use client";

import { extendTheme, ThemeConfig } from "@chakra-ui/react";

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
    global: {
      body: {
        bg: "gray.900",
        color: "gray.50"
      }
    }
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
