import React from "react";
import { Flex, Text, Button } from "@chakra-ui/react";
import PropTypes from "prop-types";

function PaginationComponent({ activePage, totalPages, setActivePage }) {
  return (
    <Flex gap={"2"} alignItems={"center"}>
      <Flex gap={"2"} maxW={"250px"} my={"10"}>
        <Button
          onClick={() => setActivePage(activePage - 1)}
          isDisabled={activePage === 1}
        >
          Prev
        </Button>
        <Button
          onClick={() => setActivePage(activePage + 1)}
          isDisabled={activePage === totalPages}
        >
          Next
        </Button>
      </Flex>
      <Flex gap={"1"}>
        <Text color={"white"}>{activePage}</Text>
        <Text color={"white"}>of</Text>
        <Text color={"white"}>{totalPages}</Text>
      </Flex>
    </Flex>
  );
}

PaginationComponent.propTypes = {
  activePage: PropTypes.number.isRequired,
  totalPages: PropTypes.number.isRequired,
  setActivePage: PropTypes.func.isRequired,
};

export default PaginationComponent;
