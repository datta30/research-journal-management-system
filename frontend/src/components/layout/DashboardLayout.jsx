import PropTypes from 'prop-types';
import {
  AppBar,
  Badge,
  Box,
  Container,
  IconButton,
  Menu,
  MenuItem,
  Toolbar,
  Typography,
} from '@mui/material';
import LogoutIcon from '@mui/icons-material/Logout';
import NotificationsIcon from '@mui/icons-material/Notifications';
import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useAuth } from '../../context/AuthContext.jsx';
import { fetchUnreadNotifications } from '../../api/notifications.js';

const DashboardLayout = ({ title, actions, children }) => {
  const { logout, user } = useAuth();
  const [anchorEl, setAnchorEl] = useState(null);
  const notificationsQuery = useQuery({
    queryKey: ['notifications', user?.id],
    queryFn: fetchUnreadNotifications,
    enabled: Boolean(user),
  });

  const notifications = notificationsQuery.data || [];
  const open = Boolean(anchorEl);

  return (
    <Box sx={{ minHeight: '100vh', bgcolor: 'background.default' }}>
      <AppBar position="static" color="primary" sx={{ mb: 3 }}>
        <Toolbar>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>
            {title}
          </Typography>
          {actions}
          <IconButton
            color="inherit"
            size="large"
            onClick={(event) => setAnchorEl(event.currentTarget)}
            aria-label="notifications"
            sx={{ mr: 1 }}
          >
            <Badge badgeContent={notifications.length} color="secondary">
              <NotificationsIcon />
            </Badge>
          </IconButton>
          <Typography variant="body2" sx={{ mr: 2 }}>
            {user?.fullName}
          </Typography>
          <IconButton color="inherit" onClick={logout} size="large" aria-label="logout">
            <LogoutIcon />
          </IconButton>
        </Toolbar>
      </AppBar>
      <Menu anchorEl={anchorEl} open={open} onClose={() => setAnchorEl(null)}>
        {notifications.length ? (
          notifications.map((notification) => (
            <MenuItem key={notification.id} onClick={() => setAnchorEl(null)}>
              <Box>
                <Typography variant="body2">{notification.message}</Typography>
                <Typography variant="caption" color="text.secondary">
                  {new Date(notification.createdAt).toLocaleString()}
                </Typography>
              </Box>
            </MenuItem>
          ))
        ) : (
          <MenuItem disabled>No new notifications</MenuItem>
        )}
      </Menu>
      <Container maxWidth="lg" sx={{ pb: 6 }}>
        {children}
      </Container>
    </Box>
  );
};

DashboardLayout.propTypes = {
  title: PropTypes.string.isRequired,
  actions: PropTypes.node,
  children: PropTypes.node,
};

DashboardLayout.defaultProps = {
  actions: null,
  children: null,
};

export default DashboardLayout;
