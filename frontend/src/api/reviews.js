import apiClient from './client';

export const fetchReviewerAssignments = async () => {
  const { data } = await apiClient.get('/reviewer/assignments');
  return data;
};

export const submitReview = async (reviewId, payload) => {
  const { data } = await apiClient.post(`/reviewer/assignments/${reviewId}/submit`, payload);
  return data;
};

export const fetchPaperReviews = async (paperId) => {
  const { data } = await apiClient.get(`/editor/papers/${paperId}/reviews`);
  return data;
};
