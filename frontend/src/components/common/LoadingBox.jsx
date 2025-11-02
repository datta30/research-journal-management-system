import { Box, CircularProgress } from '@mui/material';

const LoadingBox = () => (
  <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
    <CircularProgress />
  </Box>
);

export default LoadingBox;
