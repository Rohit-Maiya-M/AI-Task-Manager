// api.js
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/aitaskmanager',
  withCredentials: true, 
});

// Use an interceptor to inject the token into every request automatically
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Inside your api.js
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // If the backend says the token is fake/expired
      localStorage.removeItem("token");
      window.location.href = "/"; // Kick them to login
    }
    return Promise.reject(error);
  }
);

export default api;