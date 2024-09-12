import React from "react";
import PropTypes from "prop-types";

const VideoComponent = ({ id, small = true }) => {
  return (
    <iframe
      width="100%"
      height={small ? "150" : "500"}
      src={`https://www.youtube.com/embed/${id}`}
      title=""
      allowFullScreen
    ></iframe>
  );
};

VideoComponent.propTypes = {
  id: PropTypes.string.isRequired,
  small: PropTypes.bool,
};

export default VideoComponent;
