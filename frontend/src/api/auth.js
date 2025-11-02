import apiClient from './client';

export const login = async (payload) => {
  const { data } = await apiClient.post('/auth/login', payload);
  return data;
};

export const register = async (payload) => {
  const { data } = await apiClient.post('/auth/register', payload);
  return data;
};

export const refreshToken = async (refreshTokenValue) => {
  const { data } = await apiClient.post('/auth/refresh', { refreshToken: refreshTokenValue });
  return data;
};
