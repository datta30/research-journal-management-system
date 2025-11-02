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
import StatusChip from '../../components/common/StatusChip.jsx';
import LoadingBox from '../../components/common/LoadingBox.jsx';
import useRoleGuard from '../../hooks/useRoleGuard.js';
import { fetchEditorQueue } from '../../api/papers.js';

const EditorDashboard = () => {
  useRoleGuard(['EDITOR']);
  const navigate = useNavigate();

  const queueQuery = useQuery({
    queryKey: ['editor-queue'],
    queryFn: fetchEditorQueue,
  });

  let content = null;
  if (queueQuery.isLoading) {
    content = <LoadingBox />;
  } else if (queueQuery.error) {
    content = <Alert severity="error">Unable to load queue.</Alert>;
  } else if (!queueQuery.data?.length) {
    content = <Alert severity="info">No submissions awaiting action.</Alert>;
  } else {
    content = (
      <Grid container spacing={2}>
        {queueQuery.data.map((paper) => (
          <Grid item xs={12} md={6} key={paper.id}>
            <Card variant="outlined">
              <CardHeader
                title={paper.title}
                subheader={paper.editorName ? `Editor: ${paper.editorName}` : 'Unassigned'}
                action={<StatusChip status={paper.status} />}
              />
              <CardContent>
                <Stack spacing={1}>
                  <Typography variant="body2">Submitted {new Date(paper.submittedAt).toLocaleDateString()}</Typography>
                  <Button variant="text" onClick={() => navigate(`/editor/papers/${paper.id}`)}>
                    Review details
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
    <DashboardLayout title="Editor Dashboard">
      <Box>{content}</Box>
    </DashboardLayout>
  );
};

export default EditorDashboard;
