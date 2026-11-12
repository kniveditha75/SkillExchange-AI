import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import apiService from '../../services/api';
import { Plus, Users, Settings, Briefcase, Eye, ChevronRight, AlertCircle } from 'lucide-react';

const IdeaOwnerDashboard = () => {
  const [myProjects, setMyProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchMyProjects = async () => {
      try {
        const data = await apiService.myProjects();
        setMyProjects(data);
      } catch (err) {
        console.error("Failed to fetch owned projects:", err);
        setError("Unable to load your projects.");
      } finally {
        setLoading(false);
      }
    };
    fetchMyProjects();
  }, []);

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Idea Owner Dashboard</h1>
          <p className="page-subtitle">Launch projects, recruit talent, and review startup ideas.</p>
        </div>
        <button className="btn btn-primary" onClick={() => navigate('/ideaowner/create-project')}>
          <Plus size={18} /> Create New Project
        </button>
      </div>

      {error && (
        <div style={{ background: 'rgba(239, 68, 68, 0.1)', border: '1px solid rgba(239, 68, 68, 0.3)', padding: '16px', borderRadius: 'var(--border-radius-md)', color: '#f87171', marginBottom: '24px' }}>
          {error}
        </div>
      )}

      {/* Quick Access Menu */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '20px', marginBottom: '40px' }}>
        <div className="card glass-panel metric-card" style={{ cursor: 'pointer' }} onClick={() => navigate('/ideaowner/find-team')}>
          <div className="metric-icon" style={{ background: 'var(--accent-purple-glow)', color: 'var(--accent-purple)' }}>
            <Users size={24} />
          </div>
          <div>
            <div className="metric-label">AI Team Finder</div>
            <div className="metric-value">Recruit</div>
            <div style={{ fontSize: '0.75rem', color: 'var(--accent-purple)', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '4px', marginTop: '2px' }}>
              Find matched talent <ChevronRight size={12} />
            </div>
          </div>
        </div>

        <div className="card glass-panel metric-card" style={{ cursor: 'pointer' }} onClick={() => navigate('/ideaowner/find-investor')}>
          <div className="metric-icon" style={{ background: 'var(--accent-green-glow)', color: 'var(--accent-green)' }}>
            <Briefcase size={24} />
          </div>
          <div>
            <div className="metric-label">Investment Finder</div>
            <div className="metric-value">Raise Funds</div>
            <div style={{ fontSize: '0.75rem', color: 'var(--accent-green)', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '4px', marginTop: '2px' }}>
              Find local investors <ChevronRight size={12} />
            </div>
          </div>
        </div>
      </div>

      <h2 style={{ fontSize: '1.25rem', fontWeight: 600, marginBottom: '20px' }}>My Active Projects</h2>

      {loading ? (
        <div style={{ display: 'flex', justifyContent: 'center', padding: '50px 0' }}>
          <div style={{ border: '3px solid var(--border-color)', borderTop: '3px solid var(--accent-purple)', borderRadius: '50%', width: '30px', height: '30px', animation: 'spin 1s linear infinite' }}></div>
        </div>
      ) : myProjects.length === 0 ? (
        <div className="card glass-panel" style={{ padding: '60px', textAlign: 'center', color: 'var(--text-secondary)' }}>
          <AlertCircle size={40} style={{ margin: '0 auto 16px', opacity: 0.5 }} />
          <h3>You haven't created any projects yet</h3>
          <p style={{ fontSize: '0.9rem', marginTop: '8px', marginBottom: '20px' }}>Start by launching your first project using our AI assisted builder tool.</p>
          <button className="btn btn-primary" onClick={() => navigate('/ideaowner/create-project')}>
            Create First Project
          </button>
        </div>
      ) : (
        <div className="dashboard-grid">
          {myProjects.map((project) => (
            <div key={project.id} className="card glass-panel">
              <div className="card-header">
                <div>
                  <span className="badge badge-purple" style={{ marginBottom: '8px', fontSize: '0.65rem' }}>{project.category}</span>
                  <h3 className="card-title">{project.title}</h3>
                </div>
                <span className={`badge ${project.status === 'open' ? 'badge-green' : 'badge-yellow'}`} style={{ fontSize: '0.65rem' }}>
                  {project.status}
                </span>
              </div>

              <p className="card-desc">{project.description}</p>

              <div style={{ display: 'flex', gap: '16px', fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
                <span>Members: <strong>{project.member_count} / {project.team_size}</strong></span>
                {project.seeking_funding && (
                  <span>Raised: <strong style={{ color: 'var(--accent-green)' }}>${project.amount_raised.toLocaleString()}</strong></span>
                )}
              </div>

              <div style={{ display: 'flex', gap: '10px', marginTop: 'auto', borderTop: '1px solid var(--border-color)', paddingTop: '16px' }}>
                <button 
                  className="btn btn-secondary btn-sm" 
                  style={{ flex: 1 }}
                  onClick={() => navigate(`/freelancer/projects/${project.id}`)}
                >
                  <Eye size={14} /> View
                </button>
                <button 
                  className="btn btn-primary btn-sm"
                  style={{ flex: 1 }}
                  onClick={() => navigate(`/ideaowner/projects/${project.id}/manage`)}
                >
                  <Settings size={14} /> Manage
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default IdeaOwnerDashboard;
