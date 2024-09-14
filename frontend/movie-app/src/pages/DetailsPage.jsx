import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import {
  Box,
  Button,
  CircularProgress,
  CircularProgressLabel,
  Container,
  Flex,
  Heading,
  Image,
  Spinner,
  Text,
  Badge,
  useToast,
  FormControl,
  FormLabel,
  Textarea,
} from "@chakra-ui/react";
import {
  addToWatchlist,
  checkIfInWatchlist,
  fetchCredits,
  fetchDetails,
  fetchReviews,
  fetchVideos,
  imagePath,
  imagePathOriginal,
  removeFromWatchlist,
} from "../services/api";
import {
  CalendarIcon,
  CheckCircleIcon,
  SmallAddIcon,
  TimeIcon,
} from "@chakra-ui/icons";
import {
  minutesToHours,
  ratingToPercentage,
  resolveRatingColor,
} from "../utils/helpers";
import VideoComponent from "../components/VideoComponent";
import { useSelector } from "react-redux";

const DetailsPage = () => {
  const router = useParams();
  const { type, id } = router;

  const { user, token } = useSelector((state) => ({
    user: state.auth.user,
    token: state.auth.token,
  }));

  const toast = useToast();

  const [details, setDetails] = useState({});
  const [cast, setCast] = useState([]);
  const [video, setVideo] = useState(null);
  const [videos, setVideos] = useState([]);
  const [reviews, setReviews] = useState([]);
  const [newReviewText, setNewReviewText] = useState("");
  const [loading, setLoading] = useState(true);
  const [isInWatchlist, setIsInWatchlist] = useState(false);

  const handleSaveToWatchlist = async () => {
    if (!user) {
      toast({
        title: "Login to add to watchlist",
        status: "error",
        isClosable: true,
      });
      return;
    }

    const data = {
      filmId: details?.id,
      title: details?.title || details?.name,
      type: type,
      poster_path: details?.poster_path,
      release_date: details?.release_date || details?.first_air_date,
      vote_average: details?.vote_average,
      overview: details?.overview,
    };

    console.log(data);
    console.log(reviews, "reviews");
    try {
      await addToWatchlist(data, token);
      setIsInWatchlist(true);
      toast({
        title: "Added to watchlist",
        status: "success",
        isClosable: true,
      });
    } catch (error) {
      const errorMessage = error.message || "An error occurred"; // Extract the error message
      toast({
        title: errorMessage,
        status: "error",
        isClosable: true,
      });
    }
  };

  const handleRemoveFromWatchlist = async () => {
    if (!user) {
      toast({
        title: "Login to remove from watchlist",
        status: "error",
        isClosable: true,
      });
      return;
    }

    try {
      await removeFromWatchlist(id, token);
      setIsInWatchlist(false);
      toast({
        title: "Removed from watchlist",
        status: "success",
        isClosable: true,
      });
    } catch (error) {
      const errorMessage = error.message || "An error occurred";
      toast({
        title: errorMessage,
        status: "error",
        isClosable: true,
      });
    }
  };

  //   useEffect(() => {
  //     setLoading(true);
  //     fetchDetails(type, id)
  //       .then((res) => {
  //         console.log(res);

  //         setDetails(res);
  //       })
  //       .catch((err) => {
  //         console.log(err, "err");
  //       })
  //       .finally(() => {
  //         setLoading(false);
  //       });
  //   }, [type, id]);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      try {
        const [detailsData, creditsData, videosData, reviewsData] =
          await Promise.all([
            fetchDetails(type, id),
            fetchCredits(type, id),
            fetchVideos(type, id),
            fetchReviews(id),
          ]);

        setDetails(detailsData);
        setCast(creditsData?.cast?.slice(0, 10));
        const video = videosData?.results?.find(
          (video) => video?.type === "Trailer"
        );
        setVideo(video);
        const videos = videosData?.results
          ?.filter((video) => video?.type !== "Trailer")
          ?.slice(0, 10);
        setVideos(videos);
        console.log(reviews);
        setReviews(reviewsData);

        if (user) {
          const watchlistStatus = await checkIfInWatchlist(id, token);
          setIsInWatchlist(watchlistStatus === "IN");
        }
      } catch (error) {
        console.log(error, "error");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [type, id, user, token]);

  const handleReviewSubmit = async () => {
    if (!user) {
      toast({
        title: "Please login to submit a review",
        status: "error",
        isClosable: true,
      });
      return;
    }

    if (!newReviewText.trim()) {
      toast({
        title: "Review text cannot be empty",
        status: "warning",
        isClosable: true,
      });
      return;
    }

    try {
      const addedReview = await submitReview(id, newReviewText, token);
      setReviews((prevReviews) => [addedReview, ...prevReviews]);
      setNewReviewText("");
      toast({
        title: "Review submitted successfully",
        status: "success",
        isClosable: true,
      });
    } catch (error) {
      const errorMessage = error.message || "An error occurred";
      toast({
        title: errorMessage,
        status: "error",
        isClosable: true,
      });
    }
  };

  console.log(cast, "cast");
  console.log(video, "video");
  console.log(videos, "videos");

  useEffect(() => {
    if (!user) {
      setIsInWatchlist(false);
      return;
    }
    // check if in watch list
  }, [id, user]);

  if (loading) {
    return (
      <Flex justify={"center"}>
        <Spinner size={"xl"} color="red" />
      </Flex>
    );
  }

  const title = details?.title || details?.name;
  const releaseDate =
    type === "tv" ? details?.first_air_date : details?.release_date;

  return (
    <Box>
      <Box
        background={`linear-gradient(rgba(0, 0, 0, .88), rgba(0, 0, 0, .88)), url(${imagePathOriginal}${details?.backdrop_path})`}
        backgroundRepeat={"no-repeat"}
        backgroundSize={"cover"}
        backgroundPosition={"center"}
        w={"100%"}
        h={{ base: "auto", md: "500px" }}
        py={"2"}
        zIndex={"-1"}
        display={"flex"}
        alignItems={"center"}
      >
        <Container maxW={"container.xl"}>
          <Flex
            alignItems={"center"}
            gap="10"
            flexDirection={{ base: "column", md: "row" }}
          >
            <Image
              height={"450px"}
              borderRadius={"sm"}
              src={`${imagePath}${details?.poster_path}`}
            />
            <Box>
              <Heading fontSize={"3xl"} color={"white"}>
                {title}{" "}
                <Text as="span" fontWeight={"normal"} color={"gray.400"}>
                  {new Date(releaseDate).getFullYear()}
                </Text>
              </Heading>

              <Flex alignItems={"center"} gap={"4"} mt={1} mb={5}>
                <Flex alignItems={"center"}>
                  <CalendarIcon mr={2} color={"gray.400"} />
                  <Text color={"white"} fontSize={"sm"}>
                    {new Date(releaseDate).toLocaleDateString("en-US")} (US)
                  </Text>
                </Flex>
                {type === "movie" && (
                  <>
                    <Box color={"white"}>*</Box>
                    <Flex alignItems={"center"}>
                      <TimeIcon mr="2" color={"gray.400"} />
                      <Text fontSize={"sm"} color={"white"}>
                        {minutesToHours(details?.runtime)}
                      </Text>
                    </Flex>
                  </>
                )}
              </Flex>

              <Flex alignItems={"center"} gap={"4"}>
                <CircularProgress
                  value={ratingToPercentage(details?.vote_average)}
                  bg={"gray.800"}
                  borderRadius={"full"}
                  p={"0.5"}
                  size={"70px"}
                  color={resolveRatingColor(details?.vote_average)}
                  thickness={"6px"}
                >
                  <CircularProgressLabel color={"white"} fontSize={"lg"}>
                    {ratingToPercentage(details?.vote_average)}{" "}
                    <Box as="span" fontSize={"10px"}>
                      %
                    </Box>
                  </CircularProgressLabel>
                </CircularProgress>
                <Text color={"white"} display={{ base: "none", md: "initial" }}>
                  User Score
                </Text>
                <Button
                  display={isInWatchlist ? "flex" : "none"}
                  leftIcon={<CheckCircleIcon />}
                  colorScheme="green"
                  variant={"outline"}
                  onClick={handleRemoveFromWatchlist}
                >
                  In watchlist
                </Button>
                <Button
                  display={isInWatchlist ? "none" : "flex"}
                  leftIcon={<SmallAddIcon />}
                  variant={"outline"}
                  onClick={handleSaveToWatchlist}
                >
                  <Text color={"white"}>Add to watchlist</Text>
                </Button>
              </Flex>
              <Text
                color={"gray.400"}
                fontSize={"sm"}
                fontStyle={"italic"}
                my="5"
              >
                {details?.tagline}
              </Text>
              <Heading fontSize={"xl"} mb={"3"} color={"white"}>
                Overview
              </Heading>
              <Text color={"white"} fontSize={"md"} mb={"3"}>
                {details?.overview}
              </Text>
              <Flex mt={6} gap={2}>
                {details?.genres?.map((genre) => {
                  return (
                    <Badge key={genre?.id} p={1}>
                      {genre?.name}
                    </Badge>
                  );
                })}
              </Flex>
            </Box>
          </Flex>
        </Container>
      </Box>

      <Container maxW={"container.xl"} pb={"10"}>
        <Heading
          as={"h2"}
          fontSize={"md"}
          textTransform={"uppercase"}
          mt={"10"}
          color={"white"}
        >
          Cast
        </Heading>
        <Flex mt={5} mb={10} overflowX={"scroll"} gap={"5"}>
          {cast?.length === 0 && <Text>No cast found</Text>}
          {cast &&
            cast?.map((item) => {
              return (
                // TODO Add normal slider
                <Box key={item?.id} minW={"150px"}>
                  <Image
                    src={`${imagePath}${item?.profile_path}`}
                    w={"100%"}
                    height={"225px"}
                    objectFit={"cover"}
                    borderRadius={"sm"}
                  />
                </Box>
              );
            })}
        </Flex>

        <Heading
          as={"h2"}
          fontSize={"md"}
          textTransform={"uppercase"}
          mt={"10"}
          mb={"5"}
        >
          Videos
        </Heading>
        <VideoComponent id={video?.key} small={false} />

        <Heading
          as={"h2"}
          fontSize={"md"}
          textTransform={"uppercase"}
          mt={"10"}
          mb={"5"}
          color={"white"}
        >
          Reviews
        </Heading>

        {/* Add pagination */}
        {Array.isArray(reviews) && reviews.length === 0 ? (
          <Text color={"white"}>
            No reviews yet. Be the first to write one!
          </Text>
        ) : (
          reviews.map((review) => (
            <Box key={review.id} mb={4} p={4} bg="gray.700" borderRadius="md">
              <Text fontWeight="bold" color="white">
                {review.user.username}
              </Text>
              <Text fontSize="sm" color="gray.400">
                {new Date(review.created_at).toLocaleString()}
              </Text>
              <Text mt={2} color="white">
                {review.review}
              </Text>
            </Box>
          ))
        )}

        {/* Write a Review */}
        <Box mt={6}>
          <FormControl>
            <FormLabel color={"white"}>Write a Review</FormLabel>
            <Textarea
              value={newReviewText}
              onChange={(e) => setNewReviewText(e.target.value)}
              placeholder="Share your thoughts about this film..."
              bg="gray.800"
              color="white"
              mb={2}
            />
          </FormControl>
          <Button colorScheme="teal" onClick={handleReviewSubmit}>
            Submit Review
          </Button>
        </Box>
      </Container>
    </Box>
  );
};

export default DetailsPage;
