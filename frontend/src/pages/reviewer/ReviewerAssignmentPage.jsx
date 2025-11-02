import { useParams } from 'react-router-dom';
import { Alert, Box, Typography } from '@mui/material';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import DashboardLayout from '../../components/layout/DashboardLayout.jsx';
import PaperDetailContent from '../../components/common/PaperDetailContent.jsx';
import ReviewForm from '../../components/forms/ReviewForm.jsx';
import LoadingBox from '../../components/common/LoadingBox.jsx';
import useRoleGuard from '../../hooks/useRoleGuard.js';
import { fetchReviewerAssignments, submitReview } from '../../api/reviews.js';
import { fetchPaperDetail } from '../../api/papers.js';

const ReviewerAssignmentPage = () => {
  useRoleGuard(['REVIEWER']);
  const { reviewId } = useParams();
  const queryClient = useQueryClient();

  const assignmentsQuery = useQuery({
    queryKey: ['reviewer-assignments'],
    queryFn: fetchReviewerAssignments,
  });

  const assignment = assignmentsQuery.data?.find((item) => String(item.id) === String(reviewId));

  const paperQuery = useQuery({
    queryKey: ['reviewer-paper', assignment?.paperId],
    queryFn: () => fetchPaperDetail(assignment.paperId, 'reviewer'),
    enabled: Boolean(assignment?.paperId),
  });

  const submitMutation = useMutation({
    mutationFn: (payload) => submitReview(reviewId, payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['reviewer-assignments'] });
      queryClient.invalidateQueries({ queryKey: ['reviewer-paper', assignment?.paperId] });
    },
  });

  if (assignmentsQuery.isLoading || paperQuery.isLoading) {
    return (
      <DashboardLayout title="Review Assignment">
        <LoadingBox />
      </DashboardLayout>
    );
  }

  if (!assignment || paperQuery.error) {
    return (
      <DashboardLayout title="Review Assignment">
        <Alert severity="error">Assignment not found.</Alert>
      </DashboardLayout>
    );
  }

  const paper = paperQuery.data;

  return (
    <DashboardLayout title="Review Assignment">
      <PaperDetailContent paper={paper}>
        <Box sx={{ mt: 3 }}>
          <Typography variant="h6" gutterBottom>
            Submit Your Review
          </Typography>
          {assignment.status === 'SUBMITTED' ? (
            <Alert severity="info">Review already submitted.</Alert>
          ) : (
            <ReviewForm
              onSubmit={(payload) => {
                submitMutation.mutate(payload);
              }}
            />
          )}
          {submitMutation.error && <Alert severity="error">Failed to submit review.</Alert>}
        </Box>
      </PaperDetailContent>
    </DashboardLayout>
  );
};

export default ReviewerAssignmentPage;
