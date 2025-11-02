import { useNavigate } from 'react-router-dom';
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  CardHeader,
  Grid,
  Stack,
  Typography,
} from '@mui/material';
import { useQuery } from '@tanstack/react-query';
import DashboardLayout from '../../components/layout/DashboardLayout.jsx';
import LoadingBox from '../../components/common/LoadingBox.jsx';
import useRoleGuard from '../../hooks/useRoleGuard.js';
import { fetchReviewerAssignments } from '../../api/reviews.js';

const ReviewerDashboard = () => {
  useRoleGuard(['REVIEWER']);
  const navigate = useNavigate();

  const assignmentsQuery = useQuery({
    queryKey: ['reviewer-assignments'],
    queryFn: fetchReviewerAssignments,
  });

  let content = null;
  if (assignmentsQuery.isLoading) {
    content = <LoadingBox />;
  } else if (assignmentsQuery.error) {
    content = <Alert severity="error">Unable to load assignments.</Alert>;
  } else if (!assignmentsQuery.data?.length) {
    content = <Alert severity="info">No assignments yet.</Alert>;
  } else {
    content = (
      <Grid container spacing={2}>
        {assignmentsQuery.data.map((assignment) => (
          <Grid item xs={12} md={6} key={assignment.id}>
            <Card variant="outlined">
              <CardHeader title={`Paper #${assignment.paperId}`} subheader={assignment.reviewer.fullName} />
              <CardContent>
                <Stack spacing={1}>
                  <Typography variant="body2">Status: {assignment.status}</Typography>
                  <Typography variant="body2">
                    Due: {assignment.dueAt ? new Date(assignment.dueAt).toLocaleString() : 'Not set'}
                  </Typography>
                  <Button variant="text" onClick={() => navigate(`/reviewer/assignments/${assignment.id}`)}>
                    Work on review
                  </Button>
                </Stack>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
    );
  }

  return (
    <DashboardLayout title="Reviewer Workspace">
      <Box>{content}</Box>
    </DashboardLayout>
  );
};

export default ReviewerDashboard;
