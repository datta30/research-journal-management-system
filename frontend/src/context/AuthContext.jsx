import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import PropTypes from 'prop-types';
import { login as loginApi, refreshToken as refreshTokenApi, register as registerApi } from '../api/auth';

const AuthContext = createContext();

const readPersistedState = () => {
  try {
    const token = localStorage.getItem('journal_token');
    const refreshToken = localStorage.getItem('journal_refresh');
    const user = JSON.parse(localStorage.getItem('journal_user') || 'null');
    return { token, refreshToken, user };
  } catch (error) {
    return { token: null, refreshToken: null, user: null };
  }
};

export const AuthProvider = ({ children }) => {
  const [{ token, refreshToken, user }, setState] = useState(readPersistedState);

  useEffect(() => {
    localStorage.setItem('journal_token', token || '');
    localStorage.setItem('journal_refresh', refreshToken || '');
    localStorage.setItem('journal_user', JSON.stringify(user || null));
  }, [token, refreshToken, user]);

  const handleAuthSuccess = useCallback((payload) => {
    setState({
      token: payload.token,
      refreshToken: payload.refreshToken,
      user: payload.user,
    });
  }, []);

  const login = useCallback(async (credentials) => {
    const data = await loginApi(credentials);
    handleAuthSuccess(data);
    return data;
  }, [handleAuthSuccess]);

  const register = useCallback(async (payload) => {
    const data = await registerApi(payload);
    handleAuthSuccess(data);
    return data;
  }, [handleAuthSuccess]);

  const logout = useCallback(() => {
    setState({ token: null, refreshToken: null, user: null });
    localStorage.removeItem('journal_token');
    localStorage.removeItem('journal_refresh');
    localStorage.removeItem('journal_user');
  }, []);

  const refresh = useCallback(async () => {
    if (!refreshToken) {
      logout();
      return null;
    }
    const data = await refreshTokenApi(refreshToken);
    handleAuthSuccess(data);
    return data;
  }, [handleAuthSuccess, logout, refreshToken]);

  const value = useMemo(() => ({
    token,
    refreshToken,
    user,
    isAuthenticated: Boolean(token && user),
    login,
    register,
    logout,
    refresh,
    setUser: (nextUser) => setState((prev) => ({ ...prev, user: nextUser })),
  }), [login, logout, refresh, register, token, refreshToken, user]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

AuthProvider.propTypes = {
  children: PropTypes.node.isRequired,
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
