import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';

const useRoleGuard = (allowedRoles) => {
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    if (allowedRoles && allowedRoles.length && user && !allowedRoles.includes(user.role)) {
      navigate('/');
    }
  }, [isAuthenticated, user, allowedRoles, navigate]);
};

export default useRoleGuard;
