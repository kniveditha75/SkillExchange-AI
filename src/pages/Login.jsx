import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Zap } from 'lucide-react';

const Login = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const userProfile = await login(email, password);
      if (!userProfile.role) {
        navigate('/select-role');
      } else {
        // Redirect to their active workspace
        if (userProfile.role === 'freelancer') navigate('/freelancer/dashboard');
        else if (userProfile.role === 'idea_owner') navigate('/ideaowner/dashboard');
        else if (userProfile.role === 'investor') navigate('/investor/dashboard');
      }
    } catch (err) {
      console.error(err);
      const detail = err.response?.data?.detail;
      if (Array.isArray(detail)) {
        setError(detail.map(d => `${d.loc[d.loc.length - 1]}: ${d.msg}`).join(', '));
      } else if (typeof detail === 'string') {
        setError(detail);
      } else {
        setError('Invalid email or password. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-split-container">
      {/* Branding Panel - Hidden on Mobile/Tablet */}
      <div className="auth-sidebar-panel">
        <div className="auth-sidebar-grid"></div>
        <div className="auth-sidebar-header">
          <Link to="/" className="auth-sidebar-logo-brand">
            <Zap size={28} fill="#6366f1" />
            <span>SkillExchange AI</span>
          </Link>
        </div>
        
        <div className="auth-sidebar-visual-container">
          <div className="animated-logo-wrapper">
            <div className="logo-pulse-glow"></div>
            <div className="orbit-ring ring-outer">
              <div className="orbit-node node-1"></div>
              <div className="orbit-node node-2"></div>
            </div>
            <div className="orbit-ring ring-inner">
              <div className="orbit-node node-3"></div>
            </div>
            <div className="logo-center-icon">
              <Zap size={56} fill="#6366f1" className="zap-glow" />
            </div>
          </div>
          <h1 className="auth-sidebar-brand-name">SkillExchange <span>AI</span></h1>
          <p className="auth-sidebar-brand-tagline">Where Skills Meet Vision & Capital</p>
        </div>

        <div className="auth-sidebar-footer">
          &copy; {new Date().getFullYear()} SkillExchange AI. Empowering decentralized collaboration.
        </div>
      </div>

      {/* Form Panel */}
      <div className="auth-form-panel">
        <div className="auth-card glass-panel">
          <div className="auth-header">
            <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '16px' }}>
              <div style={{ background: 'var(--accent-purple-glow)', padding: '12px', borderRadius: '50%', color: 'var(--accent-purple)' }}>
                <Zap size={32} fill="#6366f1" />
              </div>
            </div>
            <h1 className="auth-title">Welcome Back</h1>
            <p className="auth-subtitle">Sign in to your SkillExchange account</p>
          </div>

          {error && (
            <div style={{ background: 'rgba(239, 68, 68, 0.1)', border: '1px solid rgba(239, 68, 68, 0.3)', padding: '12px', borderRadius: 'var(--border-radius-sm)', color: '#f87171', fontSize: '0.85rem', marginBottom: '20px', textAlign: 'center' }}>
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label className="form-label">Email Address</label>
              <input 
                type="email" 
                className="form-input" 
                placeholder="you@example.com" 
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label">Password</label>
              <input 
                type="password" 
                className="form-input" 
                placeholder="••••••••" 
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>

            <button 
              type="submit" 
              className="btn btn-primary" 
              style={{ width: '100%', padding: '12px', marginTop: '10px' }}
              disabled={loading}
            >
              {loading ? 'Authenticating...' : 'Sign In'}
            </button>
          </form>

          <div style={{ textAlign: 'center', marginTop: '24px', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
            Don't have an account? <Link to="/signup" style={{ color: 'var(--accent-purple)', textDecoration: 'none', fontWeight: 600 }}>Create Account</Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;
