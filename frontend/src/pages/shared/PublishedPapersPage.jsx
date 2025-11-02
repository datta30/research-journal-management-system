import { useQuery } from '@tanstack/react-query';
import {
  Alert,
  Box,
  Card,
  CardContent,
  CardHeader,
  Container,
  Grid,
  Typography,
} from '@mui/material';
import { fetchPublishedPapers } from '../../api/papers.js';
import StatusChip from '../../components/common/StatusChip.jsx';
import LoadingBox from '../../components/common/LoadingBox.jsx';

const PublishedPapersPage = () => {
  const publishedQuery = useQuery({
    queryKey: ['published-papers'],
    queryFn: fetchPublishedPapers,
  });

  let content = null;
  if (publishedQuery.isLoading) {
    content = <LoadingBox />;
  } else if (publishedQuery.error) {
    content = <Alert severity="error">Unable to load published papers.</Alert>;
  } else if (!publishedQuery.data?.length) {
    content = <Alert severity="info">No published papers yet.</Alert>;
  } else {
    content = (
      <Grid container spacing={2}>
        {publishedQuery.data.map((paper) => (
          <Grid item xs={12} md={6} key={paper.id}>
            <Card variant="outlined">
              <CardHeader
                title={paper.title}
                subheader={`Editor: ${paper.editorName || 'n/a'}`}
                action={<StatusChip status={paper.status} />}
              />
              <CardContent>
                <Typography variant="body2">
                  Submitted {paper.submittedAt ? new Date(paper.submittedAt).toLocaleDateString() : 'n/a'}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 6 }}>
      <Typography variant="h4" gutterBottom>
        Published Papers
      </Typography>
      <Box>{content}</Box>
    </Container>
  );
};

export default PublishedPapersPage;
