import React from "react";
import { useSelector } from "react-redux";
import PropTypes from "prop-types";
import { Navigate } from "react-router-dom";

const Protected = ({ children }) => {
  const isAuthenticated = useSelector((state) => state.auth.isAuthenticated);
  const user = useSelector((state) => state.auth.user);

  if (isAuthenticated === null && !user) {
    // While loading, you can return a spinner or a simple loading message
    return <div>Loading...</div>;
  }

  return <>{isAuthenticated ? children : <Navigate to={"/"} />}</>;
};

export default Protected;

Protected.propTypes = {
  children: PropTypes.node.isRequired,
};
