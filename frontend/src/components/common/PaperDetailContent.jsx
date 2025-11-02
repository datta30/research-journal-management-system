import PropTypes from 'prop-types';
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Box,
  Chip,
  Divider,
  Grid,
  Stack,
  Typography,
} from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import StatusChip from './StatusChip.jsx';

const PaperDetailContent = ({ paper, children }) => (
  <Stack spacing={3}>
    <Box>
      <Typography variant="h5" gutterBottom>
        {paper.title}
      </Typography>
      <Stack direction="row" spacing={2} alignItems="center" sx={{ mb: 1 }}>
        <StatusChip status={paper.status} />
        {paper.publishedAt && <Chip color="success" label="Published" />}
        <Typography variant="body2">Current Version: {paper.currentVersion}</Typography>
      </Stack>
      <Typography variant="subtitle2" color="text.secondary">
        Abstract
      </Typography>
      <Typography variant="body1" paragraph>
        {paper.abstractText}
      </Typography>
      {paper.keywords && (
        <Typography variant="body2" color="text.secondary">
          Keywords: {paper.keywords}
        </Typography>
      )}
      {paper.decisionNotes && (
        <Box sx={{ mt: 2 }}>
          <Typography variant="subtitle2">Editorial Notes</Typography>
          <Typography variant="body2">{paper.decisionNotes}</Typography>
        </Box>
      )}
    </Box>

    <Divider />

    <Box>
      <Typography variant="h6" gutterBottom>
        Versions & Plagiarism Insights
      </Typography>
      <Stack spacing={2}>
        {paper.versions.map((version) => (
          <Accordion key={version.id} defaultExpanded={version.versionNumber === paper.currentVersion}>
            <AccordionSummary expandIcon={<ExpandMoreIcon />}>
              <Typography variant="subtitle1">
                Version {version.versionNumber} — Submitted {version.submittedAt ? new Date(version.submittedAt).toLocaleString() : 'n/a'}
              </Typography>
            </AccordionSummary>
            <AccordionDetails>
              <Stack spacing={1}>
                <Typography variant="body2">Change Log: {version.changeLog || 'n/a'}</Typography>
                <Typography variant="body2">File: {version.filePath || 'n/a'}</Typography>
                <Typography variant="body2">
                  Plagiarism Score: {version.plagiarismScore != null ? `${(version.plagiarismScore * 100).toFixed(0)}%` : 'Not evaluated'}
                </Typography>
                {version.matches?.length ? (
                  <Box>
                    <Typography variant="subtitle2">Similar Works</Typography>
                    <Grid container spacing={1}>
                      {version.matches.map((match) => (
                        <Grid item key={match.paperId} xs={12} md={6}>
                          <Typography variant="body2">
                            #{match.paperId}: {match.paperTitle} ({Math.round(match.score * 100)}%)
                          </Typography>
                        </Grid>
                      ))}
                    </Grid>
                  </Box>
                ) : (
                  <Typography variant="caption" color="text.secondary">
                    No significant overlaps detected.
                  </Typography>
                )}
              </Stack>
            </AccordionDetails>
          </Accordion>
        ))}
      </Stack>
    </Box>

    {children}
  </Stack>
);

PaperDetailContent.propTypes = {
  paper: PropTypes.shape({
    id: PropTypes.number.isRequired,
    title: PropTypes.string.isRequired,
    status: PropTypes.string.isRequired,
    abstractText: PropTypes.string,
    keywords: PropTypes.string,
    currentVersion: PropTypes.number,
    publishedAt: PropTypes.string,
    decisionNotes: PropTypes.string,
    versions: PropTypes.arrayOf(
      PropTypes.shape({
        id: PropTypes.number,
        versionNumber: PropTypes.number,
        submittedAt: PropTypes.string,
        changeLog: PropTypes.string,
        filePath: PropTypes.string,
        plagiarismScore: PropTypes.number,
        matches: PropTypes.array,
      })
    ),
  }).isRequired,
  children: PropTypes.node,
};

PaperDetailContent.defaultProps = {
  children: null,
};

export default PaperDetailContent;
