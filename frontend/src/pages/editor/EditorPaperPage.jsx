import { useMemo, useState } from 'react';
import { useParams } from 'react-router-dom';
import {
  Alert,
  Button,
  Divider,
  MenuItem,
  Paper as MuiPaper,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import DashboardLayout from '../../components/layout/DashboardLayout.jsx';
import PaperDetailContent from '../../components/common/PaperDetailContent.jsx';
import LoadingBox from '../../components/common/LoadingBox.jsx';
import useRoleGuard from '../../hooks/useRoleGuard.js';
import {
  assignReviewers,
  decidePaper,
  fetchPaperDetail,
  fetchReviewers,
} from '../../api/papers.js';
import { fetchPaperReviews } from '../../api/reviews.js';

const EditorPaperPage = () => {
  useRoleGuard(['EDITOR']);
  const { paperId } = useParams();
  const queryClient = useQueryClient();

  const detailQuery = useQuery({
    queryKey: ['editor-paper', paperId],
    queryFn: () => fetchPaperDetail(paperId, 'editor'),
    enabled: Boolean(paperId),
  });

  const reviewsQuery = useQuery({
    queryKey: ['editor-paper-reviews', paperId],
    queryFn: () => fetchPaperReviews(paperId),
    enabled: Boolean(paperId),
  });

  const reviewersQuery = useQuery({
    queryKey: ['editor-reviewers'],
    queryFn: fetchReviewers,
  });

  const [assignmentForm, setAssignmentForm] = useState({ reviewerIds: [], dueAt: '' });
  const [decisionForm, setDecisionForm] = useState({ outcome: 'ACCEPT', notes: '' });

  const reviewerOptions = useMemo(() => {
    const availableReviewers = reviewersQuery.data ?? [];
    return availableReviewers.map((reviewer) => ({ label: reviewer.fullName, value: reviewer.id }));
  }, [reviewersQuery.data]);

  const assignMutation = useMutation({
    mutationFn: (payload) => assignReviewers(paperId, payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['editor-paper', paperId] });
      queryClient.invalidateQueries({ queryKey: ['editor-paper-reviews', paperId] });
    },
  });

  const decisionMutation = useMutation({
    mutationFn: (payload) => decidePaper(paperId, payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['editor-paper', paperId] });
    },
  });

  if (detailQuery.isLoading) {
    return (
      <DashboardLayout title="Submission Detail">
        <LoadingBox />
      </DashboardLayout>
    );
  }

  if (detailQuery.error) {
    return (
      <DashboardLayout title="Submission Detail">
        <Alert severity="error">Unable to load paper details.</Alert>
      </DashboardLayout>
    );
  }

  const paper = detailQuery.data;
  const reviews = reviewsQuery.data ?? [];

  return (
    <DashboardLayout title="Submission Detail">
      <PaperDetailContent paper={paper}>
        <Divider sx={{ my: 3 }} />
        <Typography variant="h6" gutterBottom>
          Reviewer Assignments
        </Typography>
        <MuiPaper variant="outlined" sx={{ p: 3, mb: 3 }}>
          <Stack spacing={2} component="form"
            onSubmit={(event) => {
              event.preventDefault();
              assignMutation.mutate({
                reviewerIds: assignmentForm.reviewerIds.map((value) => Number(value)),
                dueAt: assignmentForm.dueAt ? new Date(assignmentForm.dueAt).toISOString() : new Date().toISOString(),
              });
            }}
          >
            <TextField
              select
              SelectProps={{ multiple: true, value: assignmentForm.reviewerIds, onChange: (event) => {
                const value = event.target.value;
                setAssignmentForm((prev) => ({ ...prev, reviewerIds: Array.isArray(value) ? value : [value] }));
              } }}
              label="Reviewers"
              helperText="Select one or more reviewers"
            >
              {reviewerOptions.map((option) => (
                <MenuItem key={option.value} value={option.value}>
                  {option.label}
                </MenuItem>
              ))}
            </TextField>
            <TextField
              type="datetime-local"
              label="Due date"
              InputLabelProps={{ shrink: true }}
              value={assignmentForm.dueAt}
              onChange={(event) => setAssignmentForm((prev) => ({ ...prev, dueAt: event.target.value }))}
            />
            <Button type="submit" variant="contained" disabled={assignMutation.isPending}>
              {assignMutation.isPending ? 'Assigning…' : 'Assign Reviewers'}
            </Button>
            {assignMutation.error && <Alert severity="error">Failed to assign reviewers.</Alert>}
          </Stack>
        </MuiPaper>

        <Typography variant="h6" gutterBottom>
          Collected Reviews
        </Typography>
        {reviewsQuery.isLoading ? (
          <LoadingBox />
        ) : reviews.length ? (
          <Stack spacing={2} sx={{ mb: 3 }}>
            {reviews.map((review) => (
              <MuiPaper key={review.id} variant="outlined" sx={{ p: 2 }}>
                <Typography variant="subtitle1">Reviewer: {review.reviewer.fullName}</Typography>
                <Typography variant="body2" color="text.secondary">
                  Recommendation: {review.recommendation || 'Pending'}
                </Typography>
                <Typography variant="body2" sx={{ mt: 1 }}>
                  {review.comments || 'No comments yet.'}
                </Typography>
              </MuiPaper>
            ))}
          </Stack>
        ) : (
          <Alert severity="info" sx={{ mb: 3 }}>
            No reviews submitted yet.
          </Alert>
        )}

        <Divider sx={{ my: 3 }} />
        <Typography variant="h6" gutterBottom>
          Editorial Decision
        </Typography>
        <MuiPaper variant="outlined" sx={{ p: 3 }}>
          <Stack spacing={2} component="form"
            onSubmit={(event) => {
              event.preventDefault();
              decisionMutation.mutate(decisionForm);
            }}
          >
            <TextField
              select
              label="Outcome"
              value={decisionForm.outcome}
              onChange={(event) => setDecisionForm((prev) => ({ ...prev, outcome: event.target.value }))}
            >
              <MenuItem value="ACCEPT">Accept & Publish</MenuItem>
              <MenuItem value="REQUEST_REVISION">Request Revision</MenuItem>
              <MenuItem value="REJECT">Reject</MenuItem>
            </TextField>
            <TextField
              label="Decision Notes"
              multiline
              minRows={3}
              required
              value={decisionForm.notes}
              onChange={(event) => setDecisionForm((prev) => ({ ...prev, notes: event.target.value }))}
            />
            <Button type="submit" variant="contained" disabled={decisionMutation.isPending}>
              {decisionMutation.isPending ? 'Saving…' : 'Record Decision'}
            </Button>
            {decisionMutation.error && <Alert severity="error">Failed to record decision.</Alert>}
          </Stack>
        </MuiPaper>
      </PaperDetailContent>
    </DashboardLayout>
  );
};

export default EditorPaperPage;
