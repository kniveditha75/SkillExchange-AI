import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import apiService from '../services/api';
import { Code, Lightbulb, TrendingUp } from 'lucide-react';

const RoleSelection = () => {
  const { user, refreshProfile } = useAuth();
  const navigate = useNavigate();
  const [selectedRole, setSelectedRole] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const roles = [
    {
      id: 'freelancer',
      title: 'Freelancer',
      description: 'Work on exciting projects, showcase your technical skills, and analyze market demands with AI insights.',
      icon: <Code size={32} />
    },
    {
      id: 'idea_owner',
      title: 'Idea Owner',
      description: 'Launch new ideas, generate projects using AI, build cohesive teams, and pitch to interested investors.',
      icon: <Lightbulb size={32} />
    },
    {
      id: 'investor',
      title: 'Investor',
      description: 'Discover premium projects seeking funding, evaluate project health with AI analytics, and manage your portfolio.',
      icon: <TrendingUp size={32} />
    }
  ];

  const handleSelectRole = async () => {
    if (!selectedRole) {
      setError('Please select a role to proceed.');
      return;
    }

    setLoading(true);
    setError('');
    try {
      await apiService.saveRole({ role: selectedRole });
      await refreshProfile();
      navigate('/onboarding');
    } catch (err) {
      console.error(err);
      setError('Failed to save selected role. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="glass-panel" style={{ width: '100%', maxWidth: '800px', padding: '40px', borderRadius: 'var(--border-radius-lg)', boxShadow: 'var(--shadow-lg)' }}>
        <div className="auth-header">
          <h1 className="auth-title">Choose Your Path</h1>
          <p className="auth-subtitle">Select the persona that matches your objectives. You can change this later.</p>
        </div>

        {error && (
          <div style={{ background: 'rgba(239, 68, 68, 0.1)', border: '1px solid rgba(239, 68, 68, 0.3)', padding: '12px', borderRadius: 'var(--border-radius-sm)', color: '#f87171', fontSize: '0.85rem', marginBottom: '20px', textAlign: 'center' }}>
            {error}
          </div>
        )}

        <div className="role-grid">
          {roles.map((role) => (
            <div 
              key={role.id} 
              className={`role-card ${selectedRole === role.id ? 'selected' : ''}`}
              onClick={() => setSelectedRole(role.id)}
            >
              <div className="role-icon">
                {role.icon}
              </div>
              <h3 style={{ fontSize: '1.2rem', fontWeight: 600 }}>{role.title}</h3>
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.85rem', lineHeight: '1.4' }}>{role.description}</p>
            </div>
          ))}
        </div>

        <div style={{ display: 'flex', justifyContent: 'center', marginTop: '40px' }}>
          <button 
            className="btn btn-primary" 
            style={{ padding: '12px 40px', fontSize: '1rem' }}
            onClick={handleSelectRole}
            disabled={loading}
          >
            {loading ? 'Setting Role...' : 'Continue to Onboarding'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default RoleSelection;
