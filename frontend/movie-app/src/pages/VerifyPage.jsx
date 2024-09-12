import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { Box, Heading, Text, Button } from "@chakra-ui/react";
import { useDispatch } from "react-redux";
import { verify } from "../action/auth"; // Ensure the verify action is imported

const VerifyPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [status, setStatus] = useState("");
  const [loading, setLoading] = useState(true);

  const searchParams = new URLSearchParams(location.search);
  const token = searchParams.get("token");

  useEffect(() => {
    const handleVerification = async () => {
      try {
        dispatch(verify(token));
        setStatus("Account verified successfully!");
      } catch (error) {
        setStatus(
          "Verification failed. The token might be invalid or expirer."
        );
      } finally {
        setLoading(false);
      }
    };

    if (token) {
      handleVerification();
    }
  }, [dispatch, token]);

  if (loading) {
    return (
      <Box textAlign={"center"} mt="20">
        <Heading>Verifying...</Heading>
      </Box>
    );
  }

  return (
    <Box textAlign={"center"} mt={"20"}>
      <Heading>{status}</Heading>
      <Button
        mt={"4"}
        onClick={() => navigate("/authentication")}
        colorScheme="teal"
      >
        Go to Login
      </Button>
    </Box>
  );
};

export default VerifyPage;
