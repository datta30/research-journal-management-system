import apiClient from './client';

export const fetchAuthorPapers = async () => {
  const { data } = await apiClient.get('/author/papers');
  return data;
};

export const submitPaper = async (payload) => {
  const { data } = await apiClient.post('/author/papers', payload);
  return data;
};

export const fetchPaperDetail = async (paperId, rolePrefix) => {
  const { data } = await apiClient.get(`/${rolePrefix}/papers/${paperId}`);
  return data;
};

export const submitRevision = async (paperId, payload) => {
  const { data } = await apiClient.post(`/author/papers/${paperId}/revisions`, payload);
  return data;
};

export const fetchEditorQueue = async () => {
  const { data } = await apiClient.get('/editor/papers');
  return data;
};

export const assignReviewers = async (paperId, payload) => {
  const { data } = await apiClient.post(`/editor/papers/${paperId}/assign`, payload);
  return data;
};

export const decidePaper = async (paperId, payload) => {
  const { data } = await apiClient.post(`/editor/papers/${paperId}/decision`, payload);
  return data;
};

export const fetchPublishedPapers = async () => {
  const { data } = await apiClient.get('/papers/published');
  return data;
};

export const fetchReviewers = async () => {
  const { data } = await apiClient.get('/editor/reviewers');
  return data;
};
