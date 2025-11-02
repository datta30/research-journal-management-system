import { useState } from 'react';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import {
  Alert,
  Box,
  Button,
  Container,
  Link,
  MenuItem,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import { useAuth } from '../../context/AuthContext.jsx';

const roles = [
  { value: 'AUTHOR', label: 'Author' },
  { value: 'REVIEWER', label: 'Reviewer' },
  { value: 'EDITOR', label: 'Editor' },
];

const RegisterPage = () => {
  const navigate = useNavigate();
  const { register } = useAuth();
  const [form, setForm] = useState({
    fullName: '',
    email: '',
    password: '',
    organization: '',
    role: 'AUTHOR',
  });
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError(null);
    setLoading(true);
    try {
      await register(form);
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.message || 'Unable to register.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="sm">
      <Box component="form" onSubmit={handleSubmit} sx={{ mt: 6 }}>
        <Stack spacing={3}>
          <Typography variant="h4" align="center">
            Create your account
          </Typography>
          {error && <Alert severity="error">{error}</Alert>}
          <TextField
            label="Full Name"
            name="fullName"
            value={form.fullName}
            onChange={handleChange}
            required
          />
          <TextField
            label="Email"
            name="email"
            value={form.email}
            onChange={handleChange}
            required
            type="email"
          />
          <TextField
            label="Password"
            name="password"
            value={form.password}
            onChange={handleChange}
            required
            type="password"
            helperText="At least 8 characters"
          />
          <TextField
            label="Organization"
            name="organization"
            value={form.organization}
            onChange={handleChange}
          />
          <TextField
            select
            label="Role"
            name="role"
            value={form.role}
            onChange={handleChange}
          >
            {roles.map((role) => (
              <MenuItem key={role.value} value={role.value}>
                {role.label}
              </MenuItem>
            ))}
          </TextField>
          <Button type="submit" variant="contained" disabled={loading}>
            {loading ? 'Creating account…' : 'Register'}
          </Button>
          <Typography variant="body2" align="center">
            Already registered?{' '}
            <Link component={RouterLink} to="/login">
              Sign in
            </Link>
          </Typography>
        </Stack>
      </Box>
    </Container>
  );
};

export default RegisterPage;
