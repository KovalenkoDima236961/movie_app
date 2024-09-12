import { extendTheme } from "@chakra-ui/react";
import { mode } from "@chakra-ui/theme-tools";

const config = {
  initialColorMode: "dark",
  useSystemColorMode: false,
};

// Fix this that background will be black (--chakra-colors-white)
// const styles = {
//   global: (props) => ({
//     body: {
//       bg: mode(
//         props.theme.semanticTokens.colors["chakra-body-bg"]._light,
//         "blackAlpha.900"
//       )(props),
//     },
//   }),
// };
const styles = {
  global: (props) => ({
    body: {
      bg: mode(
        props.theme.semanticTokens.colors["chakra-body-bg"]._light,
        "blackAlpha.900"
      ),
    },
  }),
};

const theme = extendTheme({ config, styles });

export default theme;
