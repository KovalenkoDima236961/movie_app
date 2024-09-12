import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import { ChakraProvider, ColorModeScript } from "@chakra-ui/react";
import App from "./App.jsx";
import "./index.css";
import theme from "../theme.js";
import Home from "./pages/Home.jsx";
import Movies from "./pages/movies/Movies.jsx";
import Shows from "./pages/shows/Shows.jsx";
import Search from "./pages/search/Search.jsx";
import DetailsPage from "./pages/DetailsPage.jsx";
import { AuthProvider } from "./context/authProvider.jsx";
import Watchlist from "./pages/Watchlist.jsx";
import Protected from "./components/routes/Protected.jsx";
import LoginPage from "./pages/login/LoginPage.jsx";
import store from "./store.js";
import { Provider } from "react-redux";
import VerifyPage from "./pages/VerifyPage.jsx";
import ForgotPasswordPage from "./pages/ForgotPasswordPage.jsx";
import ResetPasswordPage from "./pages/ResetPasswordPage.jsx";

const router = createBrowserRouter([
  {
    path: "/",
    element: <App />,
    children: [
      {
        path: "/",
        element: <Home />,
      },
      {
        path: "/movies",
        element: <Movies />,
      },
      {
        path: "/shows",
        element: <Shows />,
      },
      {
        path: "/search",
        element: <Search />,
      },
      {
        path: "/authentication",
        element: <LoginPage />,
      },
      {
        path: "/:type/:id",
        element: <DetailsPage />,
      },
      {
        path: "/watchlist",
        element: (
          <Protected>
            <Watchlist />,
          </Protected>
        ),
      },
      {
        path: "/verify",
        element: <VerifyPage />,
      },
      {
        path: "/forgot-password",
        element: <ForgotPasswordPage />,
      },
      {
        path: "/reset-password",
        element: <ResetPasswordPage />,
      },
    ],
  },
]);

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <ColorModeScript initialColorMode={theme.config.initialColorMode} />
    <ChakraProvider theme={theme}>
      <Provider store={store}>
        <AuthProvider>
          <RouterProvider router={router} />
        </AuthProvider>
      </Provider>
    </ChakraProvider>
  </StrictMode>
);
