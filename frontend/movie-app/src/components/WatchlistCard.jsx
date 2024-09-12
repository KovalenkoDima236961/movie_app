/* eslint-disable react/prop-types */
import {
  Box,
  Flex,
  Heading,
  IconButton,
  Image,
  Text,
  Tooltip,
} from "@chakra-ui/react";
import { Link } from "react-router-dom";
import { imagePath } from "../services/api";
import { CheckIcon, StarIcon } from "@chakra-ui/icons";
import { removeFromWatchlist } from "../services/api";
import { useSelector } from "react-redux";

const WatchlistCard = ({ type, item, setWatchList }) => {
  const { user, token } = useSelector((state) => ({
    user: state.auth.user,
    token: state.auth.token,
  }));

  const handleRemoveClick = (event) => {
    event.preventDefault();
    try {
      removeFromWatchlist(item.filmId, token); // Call the API to remove the movie
      setWatchList((prev) => prev.filter((el) => el.id !== item.id)); // Update the state to remove the movie from the list
    } catch (error) {
      console.error("Failed to remove from watchlist", error);
    }
  };

  return (
    <Link to={`/${type}/${item.id}`}>
      <Flex gap="4">
        <Box position={"relative"} w={"150px"}>
          <Image
            src={`${imagePath}/${item.poster_path}`}
            alt={item.title}
            height={"200px"}
            minW={"150px"}
            objectFit={"cover"}
          />
          <Tooltip label="Remove from watchlist">
            <IconButton
              aria-label="Remove from watchlist"
              icon={<CheckIcon />}
              size={"sm"}
              colorScheme="green"
              position={"absolute"}
              zIndex={"999"}
              top="2px"
              left={"2px"}
              onClick={handleRemoveClick}
            />
          </Tooltip>
        </Box>

        <Box>
          <Heading
            color={"white"}
            fontSize={{ base: "xl", md: "2xl" }}
            noOfLines={1}
          >
            {item?.title || item?.name}
          </Heading>
          <Heading fontSize={"sm"} color={"green.200"} mt="2">
            {new Date(
              item?.release_date || item?.first_air_date
            ).getFullYear() || "N/A"}
          </Heading>
          <Flex alignItems={"center"} gap={2} mt="4">
            <StarIcon color={"yellow"} fontSize={"small"} />
            <Text color={"yellow"} textAlign={"center"} fontSize="small">
              {item?.vote_average?.toFixed(1)}
            </Text>
          </Flex>
          <Text
            color={"white"}
            mt="4"
            fontSize={{ base: "xs", md: "sm" }}
            noOfLines={5}
          >
            {item?.overview}
          </Text>
        </Box>
      </Flex>
    </Link>
  );
};

export default WatchlistCard;
