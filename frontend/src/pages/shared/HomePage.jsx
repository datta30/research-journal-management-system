import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, Button, Container, Stack, Typography } from '@mui/material';
import { useAuth } from '../../context/AuthContext.jsx';

const roleLanding = {
  AUTHOR: '/author',
  EDITOR: '/editor',
  REVIEWER: '/reviewer',
};

const HomePage = () => {
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();

  useEffect(() => {
    if (isAuthenticated && user?.role) {
      navigate(roleLanding[user.role] || '/published', { replace: true });
    }
  }, [isAuthenticated, user, navigate]);

  if (isAuthenticated && user) {
    return null;
  }

  return (
    <Container maxWidth="md" sx={{ py: 8 }}>
      <Stack spacing={3} alignItems="center">
        <Typography variant="h3" align="center">
          Research Journal Management System
        </Typography>
        <Typography variant="body1" align="center">
          Submit, review, and publish research seamlessly with integrated workflows for authors, editors, and reviewers.
        </Typography>
        <Box>
          <Button variant="contained" onClick={() => navigate('/login')} sx={{ mr: 2 }}>
            Sign In
          </Button>
          <Button variant="outlined" onClick={() => navigate('/register')}>
            Create Account
          </Button>
        </Box>
        <Button variant="text" onClick={() => navigate('/published')}>
          Browse Published Papers
        </Button>
      </Stack>
    </Container>
  );
};

export default HomePage;
