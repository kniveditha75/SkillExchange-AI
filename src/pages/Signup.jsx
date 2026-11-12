import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Zap, Code, Cpu, TrendingUp } from 'lucide-react';

const Signup = () => {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (password !== confirmPassword) {
      setError('Passwords do not match.');
      return;
    }

    setLoading(true);
    try {
      await register(email, password, fullName, phone);
      navigate('/select-role');
    } catch (err) {
      console.error(err);
      const detail = err.response?.data?.detail;
      if (Array.isArray(detail)) {
        setError(detail.map(d => `${d.loc[d.loc.length - 1]}: ${d.msg}`).join(', '));
      } else if (typeof detail === 'string') {
        setError(detail);
      } else if (err.code === 'ERR_NETWORK' || err.message === 'Network Error' || !err.response) {
        setError('Cannot connect to the server. Please make sure the backend server is running on port 8000.');
      } else {
        setError(`Registration failed (${err.response?.status || 'unknown error'}). Please try again.`);
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
        <div className="auth-sidebar-content">
          <h2 className="auth-sidebar-title">
            Connect. Collaborate. <br />Build <span>Together</span>.
          </h2>
          <p className="auth-sidebar-desc">
            Join the next-generation AI-powered skill exchange ecosystem where builders trade talent, startup ideas incubate, and venture capital flows.
          </p>
          <div className="auth-sidebar-features">
            <div className="auth-sidebar-feature">
              <div className="auth-sidebar-feature-icon">
                <Code size={20} />
              </div>
              <div className="auth-sidebar-feature-info">
                <h4 className="auth-sidebar-feature-title">Exchange Real Skills</h4>
                <p className="auth-sidebar-feature-desc">Connect with peer developers to co-build products and exchange knowledge directly.</p>
              </div>
            </div>
            <div className="auth-sidebar-feature">
              <div className="auth-sidebar-feature-icon">
                <Cpu size={20} />
              </div>
              <div className="auth-sidebar-feature-info">
                <h4 className="auth-sidebar-feature-title">AI-Driven Matchmaking</h4>
                <p className="auth-sidebar-feature-desc">Find co-founders, team members, or investors matching your specific project needs.</p>
              </div>
            </div>
            <div className="auth-sidebar-feature">
              <div className="auth-sidebar-feature-icon">
                <TrendingUp size={20} />
              </div>
              <div className="auth-sidebar-feature-info">
                <h4 className="auth-sidebar-feature-title">Incentive Alignment</h4>
                <p className="auth-sidebar-feature-desc">Form smart contract agreements, verify milestones, and secure early funding.</p>
              </div>
            </div>
          </div>
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
            <h1 className="auth-title">Create Account</h1>
            <p className="auth-subtitle">Join the SkillExchange AI ecosystem</p>
          </div>

          {error && (
            <div style={{ background: 'rgba(239, 68, 68, 0.1)', border: '1px solid rgba(239, 68, 68, 0.3)', padding: '12px', borderRadius: 'var(--border-radius-sm)', color: '#f87171', fontSize: '0.85rem', marginBottom: '20px', textAlign: 'center' }}>
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label className="form-label">Full Name</label>
              <input 
                type="text" 
                className="form-input" 
                placeholder="John Doe" 
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
                required
              />
            </div>

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
              <label className="form-label">Phone Number</label>
              <input 
                type="tel" 
                className="form-input" 
                placeholder="+1 (555) 000-0000" 
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
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

            <div className="form-group">
              <label className="form-label">Confirm Password</label>
              <input 
                type="password" 
                className="form-input" 
                placeholder="••••••••" 
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
              />
            </div>

            <button 
              type="submit" 
              className="btn btn-primary" 
              style={{ width: '100%', padding: '12px', marginTop: '10px' }}
              disabled={loading}
            >
              {loading ? 'Creating Account...' : 'Sign Up'}
            </button>
          </form>

          <div style={{ textAlign: 'center', marginTop: '24px', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
            Already have an account? <Link to="/login" style={{ color: 'var(--accent-purple)', textDecoration: 'none', fontWeight: 600 }}>Sign In</Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Signup;
