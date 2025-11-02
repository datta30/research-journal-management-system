import apiClient from './client';

export const fetchUnreadNotifications = async () => {
  const { data } = await apiClient.get('/notifications/unread');
  return data;
};
