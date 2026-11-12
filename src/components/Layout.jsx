import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Sidebar from './Sidebar';
import Header from './Header';

const Layout = () => {
  const { user, token, loading } = useAuth();

  if (loading) {
    return (
      <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: 'var(--bg-primary)' }}>
        <div style={{ textAlign: 'center' }}>
          <div style={{ border: '4px solid var(--border-color)', borderTop: '4px solid var(--accent-purple)', borderRadius: '50%', width: '40px', height: '40px', animation: 'spin 1s linear infinite', margin: '0 auto 16px' }}></div>
          <p style={{ color: 'var(--text-secondary)' }}>Loading your workspace...</p>
        </div>
        <style dangerouslySetInnerHTML={{__html: `
          @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
          }
        `}} />
      </div>
    );
  }

  // Route guard: if not logged in, redirect to login
  if (!token) {
    return <Navigate to="/login" replace />;
  }

  // Route guard: if user role is not set, force them to select a role
  if (user && !user.role) {
    // Check if the current route is already '/select-role' or '/onboarding'
    const path = window.location.pathname;
    if (path !== '/select-role' && path !== '/onboarding') {
      return <Navigate to="/select-role" replace />;
    }
  }

  return (
    <div className="app-container">
      <Sidebar />
      <div className="main-content">
        <Header />
        <main className="content-body">
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default Layout;
