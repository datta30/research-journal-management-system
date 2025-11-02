import { useParams } from 'react-router-dom';
import { Alert, Box, Divider, Typography } from '@mui/material';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import DashboardLayout from '../../components/layout/DashboardLayout.jsx';
import PaperDetailContent from '../../components/common/PaperDetailContent.jsx';
import PaperSubmissionForm from '../../components/forms/PaperSubmissionForm.jsx';
import LoadingBox from '../../components/common/LoadingBox.jsx';
import useRoleGuard from '../../hooks/useRoleGuard.js';
import { fetchPaperDetail, submitRevision } from '../../api/papers.js';

const AuthorPaperPage = () => {
  useRoleGuard(['AUTHOR']);
  const { paperId } = useParams();
  const queryClient = useQueryClient();

  const detailQuery = useQuery({
    queryKey: ['author-paper', paperId],
    queryFn: () => fetchPaperDetail(paperId, 'author'),
    enabled: Boolean(paperId),
  });

  const revisionMutation = useMutation({
    mutationFn: (payload) => submitRevision(paperId, payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['author-paper', paperId] });
    },
  });

  if (detailQuery.isLoading) {
    return (
      <DashboardLayout title="Paper Detail">
        <LoadingBox />
      </DashboardLayout>
    );
  }

  if (detailQuery.error) {
    return (
      <DashboardLayout title="Paper Detail">
        <Alert severity="error">Unable to load paper details.</Alert>
      </DashboardLayout>
    );
  }

  const paper = detailQuery.data;
  const showRevisionForm = paper.status === 'REVISIONS_REQUESTED';

  return (
    <DashboardLayout title="Paper Detail">
      <PaperDetailContent paper={paper}>
        {showRevisionForm && (
          <Box>
            <Divider sx={{ my: 3 }} />
            <Typography variant="h6" sx={{ mb: 2 }}>
              Submit Revision
            </Typography>
            <PaperSubmissionForm
              submitLabel={revisionMutation.isPending ? 'Submitting…' : 'Submit Revision'}
              onSubmit={(payload) => revisionMutation.mutate(payload)}
            />
          </Box>
        )}
      </PaperDetailContent>
    </DashboardLayout>
  );
};

export default AuthorPaperPage;
