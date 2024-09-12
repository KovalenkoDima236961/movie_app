import {
  Container,
  Grid,
  Heading,
  Flex,
  Box,
  Skeleton,
} from "@chakra-ui/react";
import { useEffect, useState } from "react";
import { fetchTrending } from "../services/api";
import CardComponent from "../components/CardComponent.jsx";

// TODO DELETE MOVIES WITH 0 RATING
const Home = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [timeWindow, setTimeWindow] = useState("day");

  useEffect(() => {
    setLoading(true);
    fetchTrending(timeWindow)
      .then((res) => {
        setData(res);
      })
      .catch((err) => {
        console.log(err, "err");
      })
      .finally(() => {
        setLoading(false);
      });
  }, [timeWindow]);

  console.log(data, "data");

  return (
    <Container maxW={"container.xl"}>
      <Flex alignItems={"baseline"} gap={"4"} my={"10"}>
        <Heading
          as="h2"
          fontSize={"md"}
          textTransform={"uppercase"}
          color={"white"}
        >
          Trending
        </Heading>
        <Flex
          alignItems={"center"}
          gap={"2"}
          border={"1px solid teal"}
          borderRadius={"20px"}
        >
          <Box
            as="button"
            px="3"
            py="1"
            borderRadius={"20px"}
            color={"white"}
            bg={`${timeWindow === "day" ? "gray.800" : ""}`}
            onClick={() => setTimeWindow("day")}
          >
            Today
          </Box>
          <Box
            as="button"
            px="3"
            py="1"
            borderRadius={"20px"}
            color={"white"}
            bg={`${timeWindow === "week" ? "gray.800" : ""}`}
            onClick={() => setTimeWindow("week")}
          >
            This Week
          </Box>
        </Flex>
      </Flex>

      <Grid
        templateColumns={{
          base: "1fr",
          sm: "repeat(2, 1fr)",
          md: "repeat(4, 1fr)",
          lg: "repeat(5, 1fr)",
        }}
        gap={"4"}
      >
        {data &&
          data.map((item, index) => {
            return loading ? (
              <Skeleton height="300" key={index} />
            ) : (
              <CardComponent
                key={item?.id}
                item={item}
                type={item?.media_type}
              />
            );
          })}
      </Grid>
    </Container>
  );
};

export default Home;
