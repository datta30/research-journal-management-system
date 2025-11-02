import axios from 'axios';

const apiBaseUrl = process.env.REACT_APP_API_URL || 'http://localhost:8080';

const apiClient = axios.create({
  baseURL: `${apiBaseUrl}/api`,
  withCredentials: true,
});

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('journal_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('journal_token');
      localStorage.removeItem('journal_refresh');
      localStorage.removeItem('journal_user');
    }
    return Promise.reject(error);
  }
);

export default apiClient;
