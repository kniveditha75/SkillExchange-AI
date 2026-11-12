import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Layout from './components/Layout';

// Auth Pages
import Welcome from './pages/Welcome';
import Login from './pages/Login';
import Signup from './pages/Signup';
import RoleSelection from './pages/RoleSelection';
import Onboarding from './pages/Onboarding';

// Freelancer Pages
import FreelancerDashboard from './pages/freelancer/FreelancerDashboard';
import ProjectMarketplace from './pages/freelancer/ProjectMarketplace';
import ProjectDetail from './pages/freelancer/ProjectDetail';
import SkillAnalysis from './pages/freelancer/SkillAnalysis';
import MyWork from './pages/freelancer/MyWork';

// Idea Owner Pages
import IdeaOwnerDashboard from './pages/ideaowner/IdeaOwnerDashboard';
import CreateProject from './pages/ideaowner/CreateProject';
import ManageProject from './pages/ideaowner/ManageProject';
import FindTeam from './pages/ideaowner/FindTeam';
import FindInvestor from './pages/ideaowner/FindInvestor';

// Investor Pages
import InvestorDashboard from './pages/investor/InvestorDashboard';
import InvestmentMarketplace from './pages/investor/InvestmentMarketplace';
import MyPortfolio from './pages/investor/MyPortfolio';

// Shared Workspace Pages
import Chat from './pages/shared/Chat';
import Notifications from './pages/shared/Notifications';
import Profile from './pages/shared/Profile';

// Dynamically redirect '/' based on authenticated user's role
const HomeRedirect = () => {
  const { user, token } = useAuth();

  if (!token) {
    return <Welcome />;
  }

  if (user) {
    if (!user.role) return <Navigate to="/select-role" replace />;
    if (user.role === 'freelancer') return <Navigate to="/freelancer/dashboard" replace />;
    if (user.role === 'idea_owner') return <Navigate to="/ideaowner/dashboard" replace />;
    if (user.role === 'investor') return <Navigate to="/investor/dashboard" replace />;
  }

  return <div style={{ color: 'var(--text-secondary)', padding: '20px' }}>Redirecting to your workspace...</div>;
};

function App() {
  return (
    <Router>
      <AuthProvider>
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<HomeRedirect />} />
          <Route path="/welcome" element={<Welcome />} />
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />

          {/* Protected Workspace Routes */}
          <Route element={<Layout />}>
            {/* Roles Selection and Profile Onboarding */}
            <Route path="/select-role" element={<RoleSelection />} />
            <Route path="/onboarding" element={<Onboarding />} />

            {/* Freelancer Views */}
            <Route path="/freelancer/dashboard" element={<FreelancerDashboard />} />
            <Route path="/freelancer/marketplace" element={<ProjectMarketplace />} />
            <Route path="/freelancer/projects/:id" element={<ProjectDetail />} />
            <Route path="/freelancer/skills" element={<SkillAnalysis />} />
            <Route path="/freelancer/my-work" element={<MyWork />} />

            {/* Idea Owner Views */}
            <Route path="/ideaowner/dashboard" element={<IdeaOwnerDashboard />} />
            <Route path="/ideaowner/create-project" element={<CreateProject />} />
            <Route path="/ideaowner/projects/:id/manage" element={<ManageProject />} />
            <Route path="/ideaowner/find-team" element={<FindTeam />} />
            <Route path="/ideaowner/find-investor" element={<FindInvestor />} />

            {/* Investor Views */}
            <Route path="/investor/dashboard" element={<InvestorDashboard />} />
            <Route path="/investor/marketplace" element={<InvestmentMarketplace />} />
            <Route path="/investor/portfolio" element={<MyPortfolio />} />

            {/* Shared Communications & Settings */}
            <Route path="/chat" element={<Chat />} />
            <Route path="/notifications" element={<Notifications />} />
            <Route path="/profile" element={<Profile />} />
          </Route>

          {/* Fallback route */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </Router>
  );
}

export default App;
