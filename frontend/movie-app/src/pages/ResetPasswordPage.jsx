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
import { useNavigate, useSearchParams } from "react-router-dom";
import * as Yup from "yup";
import { reset_password_confirm } from "../action/auth";

const PasswordResetSchema = Yup.object().shape({
  password: Yup.string()
    .min(6, "Password must be at least 6 characters")
    .max(32, "Password must not exceed 32 characters")
    .matches(
      /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{6,}$/,
      "Password must contain at least one letter and one number"
    )
    .required("Password is required"),
  confirmPassword: Yup.string()
    .oneOf([Yup.ref("password"), null], "Passwords must match")
    .required("Please confirm your password"),
});

const ResetPasswordPage = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token");
  const [successMessage, setSuccessMessage] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const handleSubmit = (values, { setSubmitting }) => {
    dispatch(reset_password_confirm(token, values.password))
      .then(() => {
        setSuccessMessage(true);
        setErrorMessage("");
        setTimeout(() => {
          navigate("/authentication");
        }, 3000);
      })
      .catch(() => {
        setErrorMessage("Failed to reset the password. Please try again.");
      })
      .finally(() => setSubmitting(false));
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
        w="100%"
        maxH={"400px"}
        p={"4"}
        bg={"white"}
        boxShadow={"lg"}
        borderRadius={"10px"}
      >
        {!successMessage ? (
          <Formik
            initialValues={{ password: "", confirmPassword: "" }}
            validationSchema={PasswordResetSchema}
            onSubmit={handleSubmit}
          >
            {({ isSubmitting }) => (
              <Form>
                <Heading mb={"4"}>Reset Password</Heading>
                {errorMessage && <Text color={"red.500"}>{errorMessage}</Text>}

                <FormControl mb={"4"}>
                  <Field
                    as={Input}
                    name="password"
                    type="password"
                    placeholder="Enter new password"
                  />
                  <ErrorMessage
                    name="password"
                    component={Text}
                    color="red.500"
                  />
                </FormControl>

                <FormControl mb={"4"}>
                  <Field
                    as={Input}
                    name="confirmPassword"
                    type="password"
                    placeholder="Confirm new password"
                  />
                  <ErrorMessage
                    name="confirmPassword"
                    component={Text}
                    color="red.500"
                  />
                </FormControl>

                <Button
                  colorScheme="blue"
                  type="submit"
                  w={"full"}
                  isLoading={isSubmitting}
                >
                  Reset Password
                </Button>
              </Form>
            )}
          </Formik>
        ) : (
          <Box textAlign={"center"}>
            <Heading size={"md"} mb={"4"} color={"green.500"}>
              Password Reset Successful
            </Heading>
            <Text>You will br redirected to the login page shortly</Text>
          </Box>
        )}
      </Box>
    </Box>
  );
};

export default ResetPasswordPage;
