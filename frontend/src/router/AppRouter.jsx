import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';
import HomePage from '../pages/shared/HomePage.jsx';
import LoginPage from '../pages/auth/LoginPage.jsx';
import RegisterPage from '../pages/auth/RegisterPage.jsx';
import PublishedPapersPage from '../pages/shared/PublishedPapersPage.jsx';
import AuthorDashboard from '../pages/author/AuthorDashboard.jsx';
import AuthorPaperPage from '../pages/author/AuthorPaperPage.jsx';
import EditorDashboard from '../pages/editor/EditorDashboard.jsx';
import EditorPaperPage from '../pages/editor/EditorPaperPage.jsx';
import ReviewerDashboard from '../pages/reviewer/ReviewerDashboard.jsx';
import ReviewerAssignmentPage from '../pages/reviewer/ReviewerAssignmentPage.jsx';

const RequireAuth = ({ children, allowedRoles }) => {
  const { isAuthenticated, user } = useAuth();
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }
  if (allowedRoles && allowedRoles.length && !allowedRoles.includes(user?.role)) {
    return <Navigate to="/" replace />;
  }
  return children;
};

const AppRouter = () => (
  <BrowserRouter>
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/published" element={<PublishedPapersPage />} />

      <Route
        path="/author"
        element={(
          <RequireAuth allowedRoles={['AUTHOR']}>
            <AuthorDashboard />
          </RequireAuth>
        )}
      />
      <Route
        path="/author/papers/:paperId"
        element={(
          <RequireAuth allowedRoles={['AUTHOR']}>
            <AuthorPaperPage />
          </RequireAuth>
        )}
      />

      <Route
        path="/editor"
        element={(
          <RequireAuth allowedRoles={['EDITOR']}>
            <EditorDashboard />
          </RequireAuth>
        )}
      />
      <Route
        path="/editor/papers/:paperId"
        element={(
          <RequireAuth allowedRoles={['EDITOR']}>
            <EditorPaperPage />
          </RequireAuth>
        )}
      />

      <Route
        path="/reviewer"
        element={(
          <RequireAuth allowedRoles={['REVIEWER']}>
            <ReviewerDashboard />
          </RequireAuth>
        )}
      />
      <Route
        path="/reviewer/assignments/:reviewId"
        element={(
          <RequireAuth allowedRoles={['REVIEWER']}>
            <ReviewerAssignmentPage />
          </RequireAuth>
        )}
      />

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  </BrowserRouter>
);

export default AppRouter;
