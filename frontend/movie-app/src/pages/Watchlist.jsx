import { useState, useEffect } from "react";
import { Container, Flex, Grid, Heading, Spinner } from "@chakra-ui/react";
import { useSelector } from "react-redux";
import { getWatchlist } from "../services/api";
import WatchlistCard from "../components/WatchlistCard";

const Watchlist = () => {
  // get watchlist
  const { user, token } = useSelector((state) => ({
    user: state.auth.user,
    token: state.auth.token,
  }));
  const [watchlist, setWatchList] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchWatchlist = async () => {
      if (user) {
        try {
          const data = await getWatchlist(token);
          setWatchList(data);
        } catch (err) {
          console.error("Error fetching watchlist:", err);
        } finally {
          setIsLoading(false);
        }
      }
    };

    fetchWatchlist();
  }, [user, token]);

  return (
    <Container maxW={"container.xl"}>
      <Flex alignItems={"baseline"} gap={"4"} my={"10"}>
        <Heading
          as="h2"
          fontSize={"md"}
          textTransform={"uppercase"}
          color={"white"}
        >
          Watchlist
        </Heading>
      </Flex>
      {isLoading && (
        <Flex justify={"center"} mt={"10"}>
          <Spinner size={"xl"} color="red" />
        </Flex>
      )}

      {!isLoading && watchlist?.length === 0 && (
        <Flex justify={"center"} mt={"10"}>
          <Heading as={"h2"} fontSize={"md"} textTransform={"uppercase"}>
            Watchlist is empty
          </Heading>
        </Flex>
      )}

      {!isLoading && watchlist?.length > 0 && (
        <Grid
          templateColumns={{
            base: "1fr",
          }}
          gap={"4"}
        >
          {watchlist?.map((item) => {
            return (
              <WatchlistCard
                key={item?.id || item?.filmId}
                item={item}
                type={item?.type}
                setWatchList={setWatchList}
              />
            );
          })}
        </Grid>
      )}
    </Container>
  );
};

export default Watchlist;
