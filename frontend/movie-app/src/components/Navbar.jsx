import {
  Avatar,
  Box,
  Button,
  Container,
  Drawer,
  DrawerBody,
  DrawerCloseButton,
  DrawerContent,
  DrawerHeader,
  DrawerOverlay,
  Flex,
  IconButton,
  Menu,
  MenuButton,
  MenuItem,
  MenuList,
  useDisclosure,
} from "@chakra-ui/react";
import React from "react";
import { Link } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux"; // Use Redux
import { login, logout } from "../action/auth"; // Import your login and logout actions
import { HamburgerIcon, SearchIcon } from "@chakra-ui/icons";
import { FiLogIn } from "react-icons/fi"; // Import Login icon from react-icons

const Navbar = () => {
  const dispatch = useDispatch();
  const user = useSelector((state) => state.auth.user); // Get user from Redux store
  const isAuthenticated = useSelector((state) => state.auth.isAuthenticated); // Get auth status
  const { onOpen, isOpen, onClose } = useDisclosure();

  const handleLogout = () => {
    dispatch(logout()); // Trigger logout action
  };

  return (
    <Box py="4" mb="2">
      <Container maxW={"container.xl"}>
        <Flex justifyContent={"space-between"}>
          <Link to="/">
            <Box
              fontSize={"2xl"}
              fontWeight={"bold"}
              color={"red"}
              letterSpacing={"widest"}
              fontFamily={"mono"} // CHANGE HERE
            >
              NETFLEX
            </Box>
          </Link>

          {/* DESKTOP */}
          <Flex
            gap="4"
            alignItems={"center"}
            display={{ base: "none", md: "flex" }}
          >
            <Link to="/">
              <Box color={"white"} letterSpacing={"widest"}>
                Home
              </Box>
            </Link>
            <Link to="/movies">
              {" "}
              <Box color={"white"} letterSpacing={"widest"}>
                Movies
              </Box>
            </Link>
            <Link to="/shows">
              <Box color={"white"} letterSpacing={"widest"}>
                TV Shows
              </Box>
            </Link>
            <Link to="/search">
              <SearchIcon fontSize={"xl"} />
            </Link>
            {isAuthenticated && user ? (
              <Menu>
                <MenuButton>
                  <Avatar
                    bg={"red.500"}
                    color={"white"}
                    size={"sm"}
                    name={user?.email}
                  />
                </MenuButton>
                <MenuList>
                  <Link to={"/watchlist"}>
                    <MenuItem>watchlist</MenuItem>
                  </Link>
                  <MenuItem onClick={handleLogout}>Logout</MenuItem>
                </MenuList>
              </Menu>
            ) : (
              <Link to="/authentication">
                {/* Navigate to login page */}
                <IconButton
                  icon={<FiLogIn />} // Login icon
                  aria-label="Login"
                  bg="gray.800"
                  color="white"
                />
              </Link>
            )}
          </Flex>

          {/* Mobile */}
          <Flex
            display={{ base: "flex", md: "none" }}
            alignItems={"center"}
            gap="4"
          >
            <Link to="/search">
              <SearchIcon fontSize={"xl"} />
            </Link>
            <IconButton onClick={onOpen} icon={<HamburgerIcon />} />
            <Drawer isOpen={isOpen} placement="right" onClose={onClose}>
              <DrawerOverlay />
              <DrawerContent bg={"black"}>
                <DrawerCloseButton />
                <DrawerHeader>
                  {isAuthenticated && user ? (
                    <Flex alignItems="center" gap="2">
                      <Avatar bg="red.500" size={"sm"} name={user?.email} />
                      <Box fontSize={"sm"}>
                        {user?.displayName || user?.email}
                      </Box>
                    </Flex>
                  ) : (
                    <Link to="/authentication">
                      {/* Navigate to login page */}
                      <IconButton
                        icon={<FiLogIn />} // Login icon for mobile
                        aria-label="Login"
                        bg="gray.800"
                        color="white"
                      />
                    </Link>
                  )}
                </DrawerHeader>

                <DrawerBody>
                  <Flex flexDirection={"column"} gap={"4"} onClick={onClose}>
                    <Link to="/">Home</Link>
                    <Link to="/movies">Movies</Link>
                    <Link to="/shows">TV Shows</Link>
                    {isAuthenticated && user && (
                      <>
                        <Link to="/watchlist">Watchlist</Link>
                        <Button
                          variant={"outline"}
                          colorScheme="red"
                          onClick={handleLogout}
                        >
                          Logout
                        </Button>
                      </>
                    )}
                  </Flex>
                </DrawerBody>
              </DrawerContent>
            </Drawer>
          </Flex>
        </Flex>
      </Container>
    </Box>
  );
};

export default Navbar;
