import { Box, Image, Text, Flex } from "@chakra-ui/react";
import { StarIcon } from "@chakra-ui/icons";
import { Link } from "react-router-dom";
import { imagePath } from "../services/api";

const CardComponent = ({ item, type }) => {
  return (
    <Link to={`/${type}/${item?.id}`}>
      <Box
        cursor={"pointer"}
        position={"relative"}
        transform={"scale(1)"}
        _hover={{
          transform: { base: "scale(1)", md: "scale(1.08)" },
          transition: "transform 0.2s ease-in-out",
          zIndex: "10",
          "& .overlay": {
            opacity: 1,
          },
        }}
      >
        <Image
          cursor={"pointer"}
          src={`${imagePath}${item.poster_path}`}
          alt={item?.title || item?.name}
          height={"100%"}
        />
        <Box
          className="overlay"
          position={"absolute"}
          p="2"
          bottom={"0"}
          left={"0"}
          w={"100%"}
          h={"33%"}
          bg={"rgba(0,0,0,0.9)"}
          color={"white"}
          opacity={"0"}
          transition={"opacity 0.3s ease-in-out"}
        >
          <Text textAlign={"center"}>{item?.title || item?.name}</Text>
          <Text textAlign={"center"} fontSize={"x-small"} color={"green.200"}>
            {new Date(
              item?.release_date || item?.first_air_date
            ).getFullYear() || "N/A"}
          </Text>
          <Flex
            alignItems={"center"}
            justifyContent={"center"}
            gap={"2"}
            mt={"4"}
          >
            <StarIcon fontSize={"small"} />
            <Text>{item?.vote_average?.toFixed(1)}</Text>
          </Flex>
        </Box>
      </Box>
    </Link>
  );
};

export default CardComponent;
