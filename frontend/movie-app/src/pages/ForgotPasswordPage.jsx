import React, { useState } from "react";
import {
  Box,
  Button,
  Container,
  FormControl,
  Input,
  Heading,
  Text,
} from "@chakra-ui/react";
import { Formik, Form, Field, ErrorMessage } from "formik";
import { useDispatch } from "react-redux";
import { reset_password } from "../action/auth";

const ForgotPasswordPage = () => {
  const dispatch = useDispatch();
  const [emailSent, setEmailSent] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const handleSubmit = (values, { setSubmitting }) => {
    dispatch(reset_password(values.email))
      .then(() => {
        setEmailSent(true);
        setErrorMessage("");
      })
      .catch(() => {
        setErrorMessage("Failed to send reset email. Please try again.");
      })
      .finally(() => {
        setSubmitting(false);
      });
  };

  return (
    <Box
      display={"flex"}
      alignItems={"center"}
      justifyContent={"center"}
      minH={"100vh"}
      bg={"gray.100"}
    >
      <Box
        w={"100%"}
        maxW={"400px"}
        p={"6"}
        bg={"white"}
        boxShadow={"lg"}
        borderRadius={"10px"}
      >
        {!emailSent ? (
          <Formik initialValues={{ email: "" }} onSubmit={handleSubmit}>
            {({ isSubmitting }) => (
              <Form>
                <Heading mb={"4"}>Forgot Password</Heading>
                {errorMessage && <Text color={"red.500"}>{errorMessage}</Text>}

                <FormControl mb={"4"}>
                  <Field
                    as="Input"
                    name="email"
                    type="email"
                    placeholder="Enter your email"
                  />
                  <ErrorMessage name="email" component={Text} color="red.500" />
                </FormControl>

                <Button
                  colorScheme="blue"
                  type="submit"
                  w={"full"}
                  isLoading={isSubmitting}
                >
                  Send Reset Email
                </Button>
              </Form>
            )}
          </Formik>
        ) : (
          <Box textAlign="center">
            <Heading size="md" mb={4} color="green.500">
              Email Sent
            </Heading>
            <Text>
              Please check your inbox for instructions to reset your password.
            </Text>
          </Box>
        )}
      </Box>
    </Box>
  );
};

export default ForgotPasswordPage;
