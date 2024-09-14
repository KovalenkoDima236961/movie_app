import axios from "axios";

export const imagePath = "https://image.tmdb.org/t/p/w500";
export const imagePathOriginal = "https://image.tmdb.org/t/p/original";

const baseUrl = "https://api.themoviedb.org/3";
const apiKey = import.meta.env.VITE_API_KEY;

const backendUrl = import.meta.env.BACKEND_URL;
// TRENDING
export const fetchTrending = async (timeWindow = "day") => {
  console.log(baseUrl);
  const { data } = await axios.get(
    `${baseUrl}/trending/all/${timeWindow}?api_key=${apiKey}`
  );

  return data?.results;
};

// MOVIES & SERIES - Details

export const fetchDetails = async (type, id) => {
  const res = await axios.get(`${baseUrl}/${type}/${id}?api_key=${apiKey}`);
  return res?.data;
};

// MOVIES & SERIES - Credits

export const fetchCredits = async (type, id) => {
  const res = await axios.get(
    `${baseUrl}/${type}/${id}/credits?api_key=${apiKey}`
  );

  return res.data;
};

// MOVIES & SERIES - Videos

export const fetchVideos = async (type, id) => {
  const res = await axios.get(
    `${baseUrl}/${type}/${id}/videos?api_key=${apiKey}`
  );

  return res?.data;
};

// DISCOVER

export const fetchMovies = async (page, sortBy) => {
  const res = await axios.get(
    `${baseUrl}/discover/movie?api_key=${apiKey}&page=${page}&sort_by=${sortBy}`
  );

  return res?.data;
};

export const fetchTvSeries = async (page, sortBy) => {
  const res = await axios.get(
    `${baseUrl}/discover/tv?api_key=${apiKey}&page=${page}&sort_by=${sortBy}`
  );

  return res?.data;
};

// SEARCH

export const searchData = async (query, page) => {
  const res = await axios.get(
    `${baseUrl}/search/multi?api_key=${apiKey}&query=${query}&page=${page}`
  );

  return res?.data;
};

// worked
export const addToWatchlist = async (filmData, token) => {
  console.log(token);
  try {
    const res = await axios.post(`${backendUrl}/api/addwatchlist`, filmData, {
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
    });
    return res.data;
  } catch (error) {
    throw error.response.data;
  }
};

export const checkIfInWatchlist = async (filmId, token) => {
  console.log(filmId, "filmId");
  console.log(token, "token");
  try {
    const res = await axios.get(`${backendUrl}/api/inwatchlist/${filmId}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    return res.data;
  } catch (error) {
    throw error.response.data;
  }
};

export const removeFromWatchlist = async (filmId, token) => {
  try {
    const res = await axios.delete(
      `${backendUrl}/api/removewatchlist/${filmId}`,
      {
        headers: { Authorization: `Bearer ${token}` },
      }
    );
    return res.data;
  } catch (error) {
    throw error.response.data;
  }
};

export const getWatchlist = async (token) => {
  console.log(token);
  console.log(localStorage.getItem("token"));
  try {
    const res = await axios.get(`${backendUrl}/api/watchlist`, {
      headers: {
        Authorization: `Bearer ${token}`, // Ensure token is passed correctly
      },
    });
    return res.data;
  } catch (error) {
    console.error("Error fetching watchlist: ", error);
    throw error;
  }
};

export const fetchReviews = async (filmId) => {
  console.log("Fetching reviews for film ID:", filmId);
  try {
    const res = await axios.get(`http://localhost:8080/reviews/${filmId}`);
    return res.data; // This will return an empty array if no reviews exist
  } catch (error) {
    if (error.response && error.response.status === 404) {
      console.log("No reviews found for this film.");
      return []; // Return an empty array if no reviews are found
    }
    throw error.response.data; // Throw other errors
  }
};

export const submitReview = async (filmId, reviewText, token) => {
  try {
    const res = await axios.post(
      `${backendUrl}/reviews/add`,
      { filmId, reviewText },
      {
        headers: { Authorization: `Bearer ${token}` },
      }
    );
    return res.data;
  } catch (error) {
    throw error.response.data;
  }
};
