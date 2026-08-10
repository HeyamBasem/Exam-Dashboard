import axios from "axios";

// New imports for navigation helper
let navigateFn = null;
export const setNavigate = (navigate) => {
  navigateFn = navigate;
};

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000,
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);

api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem("token");

      // Use the React Router navigation instead of a full page reload
      if (navigateFn) {
        navigateFn("/login", { replace: true });
      } else {
        window.location.assign("/login");
      }
    }
    return Promise.reject(error);
  },
);

export default api;
