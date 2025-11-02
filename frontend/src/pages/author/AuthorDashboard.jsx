import { useMemo, useState } from 'react';
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  CardHeader,
  Divider,
  Grid,
  Stack,
  Typography,
} from '@mui/material';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import DashboardLayout from '../../components/layout/DashboardLayout.jsx';
import PaperSubmissionForm from '../../components/forms/PaperSubmissionForm.jsx';
import StatusChip from '../../components/common/StatusChip.jsx';
import LoadingBox from '../../components/common/LoadingBox.jsx';
import useRoleGuard from '../../hooks/useRoleGuard.js';
import { fetchAuthorPapers, submitPaper } from '../../api/papers.js';
import { useNavigate } from 'react-router-dom';

const AuthorDashboard = () => {
  useRoleGuard(['AUTHOR']);
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [showForm, setShowForm] = useState(false);

  const papersQuery = useQuery({
    queryKey: ['author-papers'],
    queryFn: fetchAuthorPapers,
  });

  const submitMutation = useMutation({
    mutationFn: submitPaper,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['author-papers'] });
      setShowForm(false);
    },
  });

  const content = useMemo(() => {
    if (papersQuery.isLoading) {
      return <LoadingBox />;
    }
    if (papersQuery.error) {
      return <Alert severity="error">Failed to load papers.</Alert>;
    }
    if (!papersQuery.data?.length) {
      return <Alert severity="info">Submit your first paper to get started.</Alert>;
    }
    return (
      <Grid container spacing={2}>
        {papersQuery.data.map((paper) => (
          <Grid item xs={12} md={6} key={paper.id}>
            <Card variant="outlined">
              <CardHeader
                title={paper.title}
                subheader={`Submitted ${new Date(paper.submittedAt).toLocaleDateString()}`}
                action={<StatusChip status={paper.status} />}
              />
              <CardContent>
                <Stack spacing={1}>
                  <Typography variant="body2">Current Version: {paper.currentVersion || 1}</Typography>
                  {paper.editorName && (
                    <Typography variant="body2">Editor: {paper.editorName}</Typography>
                  )}
                  <Button variant="text" size="small" onClick={() => navigate(`/author/papers/${paper.id}`)}>
                    View details
                  </Button>
                </Stack>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
    );
  }, [papersQuery, navigate]);

  return (
    <DashboardLayout
      title="Author Portal"
      actions={
        <Button variant="contained" onClick={() => setShowForm((prev) => !prev)}>
          {showForm ? 'Close form' : 'New Submission'}
        </Button>
      }
    >
      <Stack spacing={3}>
        {showForm && (
          <Box>
            <Typography variant="h6" sx={{ mb: 2 }}>
              Submit New Paper
            </Typography>
            <PaperSubmissionForm
              submitLabel={submitMutation.isPending ? 'Submitting…' : 'Submit Paper'}
              onSubmit={(payload) => submitMutation.mutate(payload)}
            />
          </Box>
        )}
        <Divider />
        <Box>
          <Typography variant="h6" sx={{ mb: 2 }}>
            My Submissions
          </Typography>
          {content}
        </Box>
      </Stack>
    </DashboardLayout>
  );
};

export default AuthorDashboard;
