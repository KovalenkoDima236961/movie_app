import React, { useEffect, useState } from "react";
import {
  Container,
  Heading,
  Grid,
  Skeleton,
  Flex,
  Select,
} from "@chakra-ui/react";
import { fetchMovies } from "../../services/api";
import CardComponent from "../../components/CardComponent.jsx";
import PaginationComponent from "../../components/PaginationComponent.jsx";

// TODO: Треба зробити так, щоб коли людина повернулася назад, то вона була ж на тій самій сторінці
const Movies = () => {
  const [movies, setMovies] = useState([]);
  const [activePage, setActivePage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [sortBy, setSortBy] = useState("popularity.desc");
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    setIsLoading(true);
    fetchMovies(activePage, sortBy)
      .then((res) => {
        setMovies(res?.results);
        setActivePage(res?.page);
        setTotalPages(res?.total_pages);
      })
      .catch((err) => console.log(err, "error"))
      .finally(() => {
        setIsLoading(false);
      });
  }, [activePage, sortBy]);

  return (
    <Container maxW={"container.xl"}>
      <Flex alignItems={"baseline"} gap={"4"} my={"10"}>
        <Heading
          as="h2"
          fontSize={"md"}
          textTransform={"uppercase"}
          color={"white"}
        >
          Discover Movies
        </Heading>

        <Select
          w={"130px"}
          onChange={(e) => {
            setActivePage(1);
            setSortBy(e.target.value);
          }}
          color={"white"}
        >
          <option value="popularity.desc">Popular</option>
          <option value="vote_average.desc&vote_count.gte=1000">
            Top Rated
          </option>
        </Select>
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
        {movies &&
          movies.map((item, index) => {
            return isLoading ? (
              <Skeleton height="300" key={index} />
            ) : (
              <CardComponent key={item?.id} item={item} type={"movie"} />
            );
          })}
      </Grid>

      {/* Pagination */}
      <PaginationComponent
        activePage={activePage}
        totalPages={totalPages}
        setActivePage={setActivePage}
      />
    </Container>
  );
};

export default Movies;
