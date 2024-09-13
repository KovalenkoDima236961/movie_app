import React, { useEffect, useState } from "react";
import { useGoogleLogin } from "@react-oauth/google";
import {
  Box,
  Button,
  Container,
  FormControl,
  FormLabel,
  Input,
  Heading,
  Text,
  Stack,
  FormErrorMessage,
} from "@chakra-ui/react";
import { Link, Navigate } from "react-router-dom";
import { Formik, Form, Field, ErrorMessage } from "formik";
import { useDispatch, useSelector } from "react-redux";
import { login, signup, googleLogin } from "../../action/auth"; // Ensure actions are imported
import { registrationSchema, loginSchema } from "../../validation/validation";

const LoginPage = () => {
  const [signIn, setSignIn] = useState(true);
  const dispatch = useDispatch();
  const isAuthenticated = useSelector((state) => state.auth.isAuthenticated);
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState(false);

  const login = useGoogleLogin({
    onSuccess: (tokenResponse) => {
      // Dispatch googleLogin action with the credential
      dispatch(googleLogin(tokenResponse.access_token));
    },
    onError: () => {
      console.log("Google login failed");
    },
  });

  const handleSignup = (values, { setSubmitting }) => {
    dispatch(signup(values.username, values.email, values.password))
      .then(() => {
        setSuccessMessage(true);
        setErrorMessage("");

        setTimeout(() => {
          setSignIn(true);
          setSuccessMessage(false);
        }, 5000);
      })
      .catch((err) => {
        if (err.response?.status === 409) {
          setErrorMessage("User already exists.");
        } else {
          setErrorMessage("Failed to register. Please try again.");
        }
      })
      .finally(() => setSubmitting(false));
  };

  // Handle Google Login by dispatching googleLogin action
  const handleGoogleLogin = () => {};

  const handleLogin = (values, { setSubmitting }) => {
    dispatch(login(values.email, values.password))
      .catch((err) => {
        if (err.response?.status === 403) {
          setErrorMessage("Your account is not verified. Check your email.");
        } else if (err.response?.status === 400) {
          setErrorMessage("Incorrect email or password.");
        } else {
          setErrorMessage("Login failed. Please try again.");
        }
      })
      .finally(() => setSubmitting(false));
  };

  if (isAuthenticated) {
    return <Navigate to="/" />;
  }

  return (
    <Box
      display={"flex"}
      alignItems={"center"}
      justifyContent={"center"}
      minH={"100vh"}
      bg={"black"}
    >
      {/* Components.Container */}
      <Box
        position={"relative"}
        w="100%"
        maxW="678px"
        borderRadius={"10px"}
        boxShadow={"lg"}
        overflow={"hidden"}
        bg="white"
        minH={"400px"}
      >
        {/* Show success message after registration */}
        {successMessage && (
          <Box
            position={"absolute"}
            w={"100%"}
            h={"100%"}
            display={"flex"}
            justifyContent={"center"}
            alignItems={"center"}
            zIndex={"20"}
            bg={"white"}
            textAlign={"center"}
            p={"4"}
          >
            <Box>
              <Heading mb={4} color={"green.500"}>
                Account Created!
              </Heading>
              <Text mb={"4"}>
                Please check your email to confirm your account.
              </Text>
              <Text>
                You will be redirected to the login section in a few seconds...
              </Text>
            </Box>
          </Box>
        )}

        {/* Sign Up Section Components.SignUpContainer signinIn={signIn} */}
        {!successMessage && (
          <Box
            position={"absolute"}
            top={"0"}
            left={"0"}
            w={"50%"}
            h={"100%"}
            opacity={signIn ? 0 : 1}
            zIndex={signIn ? 1 : 5}
            transform={!signIn && "translateX(100%)"}
            transition={"all 0.6s ease-in-out"}
          >
            <Container>
              <Formik
                initialValues={{ username: "", email: "", password: "" }}
                validationSchema={registrationSchema}
                onSubmit={handleSignup}
              >
                {({ isSubmitting }) => (
                  <Form>
                    <Heading mb={"4"}>Create Account</Heading>
                    {errorMessage && (
                      <Text color="red.500">{errorMessage}</Text>
                    )}

                    <FormControl mb={"4"}>
                      <Field
                        as={Input}
                        name="username"
                        placeholder="Username"
                      />
                      <ErrorMessage
                        name="username"
                        component={FormErrorMessage}
                      />
                    </FormControl>

                    <FormControl mb={"4"}>
                      <Field as={Input} name="email" placeholder="Email" />
                      <ErrorMessage name="email" component={FormErrorMessage} />
                    </FormControl>

                    <FormControl mb={"4"}>
                      <Field
                        as={Input}
                        name="password"
                        type="password"
                        placeholder="Password"
                      />
                      <ErrorMessage
                        name="password"
                        component={FormErrorMessage}
                      />
                    </FormControl>

                    <Button
                      colorScheme="red"
                      w={"full"}
                      type="submit"
                      isLoading={isSubmitting}
                    >
                      Sign Up
                    </Button>
                  </Form>
                )}
              </Formik>
            </Container>
          </Box>
        )}

        {/* Sign In Section  SignInContainer */}
        {!successMessage && (
          <Box
            position={"absolute"}
            top={"0"}
            left={"0"}
            w={"50%"}
            h={"100%"}
            zIndex={signIn ? 2 : -1}
            transform={signIn ? "translateX(0)" : "translateX(100%)"}
            transition={"all 0.6s ease-in-out"}
          >
            <Container>
              <Formik
                initialValues={{ email: "", password: "" }}
                validationSchema={loginSchema}
                onSubmit={handleLogin}
              >
                {({ isSubmitting }) => (
                  <Form>
                    <Heading mb={"4"}>Sign in</Heading>
                    {errorMessage && (
                      <Text color="red.500">{errorMessage}</Text>
                    )}

                    <FormControl mb={"4"}>
                      <Field as={Input} name="email" placeholder="Email" />
                      <ErrorMessage name="email" component={FormErrorMessage} />
                    </FormControl>

                    <FormControl mb={"4"}>
                      <Field
                        as={Input}
                        name="password"
                        type="password"
                        placeholder="Password"
                      />
                      <ErrorMessage
                        name="password"
                        component={FormErrorMessage}
                      />
                    </FormControl>

                    <Link
                      to="/forgot-password"
                      style={{ marginBottom: "16px", display: "block" }}
                    >
                      Forgot your password
                    </Link>

                    <Button
                      colorScheme="red"
                      w={"full"}
                      type="submit"
                      isLoading={isSubmitting}
                    >
                      Sign In
                    </Button>
                    {/* Custom Google login button placeholder before actual Google button is loaded */}
                    <Box>
                      <Button
                        mt={4}
                        colorScheme="red"
                        w="full"
                        onClick={() => login()} // Manually trigger Google login when button is clicked
                      >
                        Continue with Google
                      </Button>
                    </Box>
                  </Form>
                )}
              </Formik>
            </Container>
          </Box>
        )}

        {/* Overlay */}
        {!successMessage && (
          <Box
            position={"absolute"}
            top={"0"}
            left={"50%"}
            w={"50%"}
            h={"100%"}
            overflow={"hidden"}
            transition={"transform 0.6s ease-in-out"}
            zIndex={"100"}
            transform={signIn ? "translateX(0)" : "translateX(-100%)"}
          >
            <Box
              bgGradient={"linear(to-r, red.500, pink.400)"}
              color={"white"}
              h={"100%"}
              w={"200%"}
              position={"absolute"}
              left={"-100%"}
              transform={signIn ? "translateX(0)" : "translateX(50%)"}
              transition={"transform 0.6s ease-in-out"}
            >
              {/* Left Panel */}
              <Stack
                position={"absolute"}
                top={"0"}
                h={"100%"}
                w={"50%"}
                p={"40px"}
                textAlign={"center"}
                justify={"center"}
                transition={"transform 0.6s ease-in-out"}
                transform={signIn ? "translateX(-20%)" : "translateX(0)"}
              >
                <Heading>Welcome Back!</Heading>
                <Text>
                  To keep connected with us, please login with your personal
                  info
                </Text>
                <Button
                  variant={"outline"}
                  colorScheme="whiteAlpha"
                  onClick={() => setSignIn(true)}
                >
                  Sign In
                </Button>
              </Stack>

              {/* Right Panel */}
              <Stack
                position={"absolute"}
                top={"0"}
                right={"0"}
                h={"100%"}
                w={"50%"}
                p="40px"
                textAlign={"center"}
                justify={"center"}
                transform={signIn ? "translateX(0)" : "translateX(20%)"}
                transition={"transform 0.6s ease-in-out"}
              >
                <Heading>Hello, Friend!</Heading>
                <Text>
                  Enter your personal data and start your journey with us!
                </Text>
              </Stack>
            </Box>
          </Box>
        )}
      </Box>
    </Box>
  );
};

export default LoginPage;
