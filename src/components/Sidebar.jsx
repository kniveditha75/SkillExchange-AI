import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { 
  LayoutDashboard, 
  Briefcase, 
  Code, 
  LineChart, 
  MessageSquare, 
  Bell, 
  User, 
  PlusCircle, 
  Users, 
  TrendingUp, 
  LogOut, 
  Zap,
  DollarSign
} from 'lucide-react';

const Sidebar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const getRoleLabel = (role) => {
    if (role === 'idea_owner') return 'Idea Owner';
    if (role === 'freelancer') return 'Freelancer';
    if (role === 'investor') return 'Investor';
    return '';
  };

  const menuItems = {
    freelancer: [
      { path: '/freelancer/dashboard', label: 'Dashboard', icon: <LayoutDashboard size={20} /> },
      { path: '/freelancer/marketplace', label: 'Browse Projects', icon: <Briefcase size={20} /> },
      { path: '/freelancer/my-work', label: 'My Applications', icon: <Code size={20} /> },
      { path: '/freelancer/skills', label: 'Skill Analysis', icon: <LineChart size={20} /> },
    ],
    idea_owner: [
      { path: '/ideaowner/dashboard', label: 'Dashboard', icon: <LayoutDashboard size={20} /> },
      { path: '/ideaowner/create-project', label: 'Create Project', icon: <PlusCircle size={20} /> },
      { path: '/ideaowner/find-team', label: 'Recruit Team', icon: <Users size={20} /> },
      { path: '/ideaowner/find-investor', label: 'Find Investors', icon: <DollarSign size={20} /> },
    ],
    investor: [
      { path: '/investor/dashboard', label: 'Dashboard', icon: <LayoutDashboard size={20} /> },
      { path: '/investor/marketplace', label: 'Funding Marketplace', icon: <TrendingUp size={20} /> },
      { path: '/investor/portfolio', label: 'My Portfolio', icon: <DollarSign size={20} /> },
    ],
  };

  const role = user?.role || 'freelancer';
  const roleMenuItems = menuItems[role] || [];

  // Common shared items
  const sharedMenuItems = [
    { path: '/chat', label: 'Conversations', icon: <MessageSquare size={20} /> },
    { path: '/notifications', label: 'Notifications', icon: <Bell size={20} /> },
    { path: '/profile', label: 'My Profile', icon: <User size={20} /> },
  ];

  return (
    <aside className="sidebar">
      <div className="sidebar-logo">
        <span className="logo-text">
          <Zap size={24} fill="#6366f1" color="#6366f1" /> SkillExchange AI
        </span>
      </div>
      
      <ul className="sidebar-menu">
        <div style={{ padding: '0 16px 8px', fontSize: '0.75rem', fontWeight: 600, color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
          Role Views
        </div>
        
        {roleMenuItems.map((item) => (
          <li key={item.path} className="sidebar-item">
            <NavLink to={item.path} className={({ isActive }) => isActive ? 'active' : ''}>
              {item.icon}
              <span>{item.label}</span>
            </NavLink>
          </li>
        ))}

        <div style={{ margin: '16px 0 8px', padding: '0 16px 8px', fontSize: '0.75rem', fontWeight: 600, color: 'var(--text-muted)', borderTop: '1px solid var(--border-color)', paddingTop: '16px', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
          Workspace
        </div>

        {sharedMenuItems.map((item) => (
          <li key={item.path} className="sidebar-item">
            <NavLink to={item.path} className={({ isActive }) => isActive ? 'active' : ''}>
              {item.icon}
              <span>{item.label}</span>
            </NavLink>
          </li>
        ))}
      </ul>

      <div className="sidebar-footer">
        {user && (
          <div className="user-profile-badge glass-panel">
            <div className="user-avatar">
              {user.profile_picture ? (
                <img 
                  src={user.profile_picture.startsWith('http') ? user.profile_picture : `http://localhost:8000/${user.profile_picture}`} 
                  alt={user.full_name} 
                  className="user-avatar"
                  style={{ border: 'none' }}
                />
              ) : (
                user.full_name.charAt(0).toUpperCase()
              )}
            </div>
            <div className="user-info">
              <div className="user-name">{user.full_name}</div>
              <div className="user-role">{getRoleLabel(user.role)}</div>
            </div>
          </div>
        )}
        <button className="btn btn-secondary" style={{ width: '100%' }} onClick={handleLogout}>
          <LogOut size={16} /> Logout
        </button>
      </div>
    </aside>
  );
};

export default Sidebar;
