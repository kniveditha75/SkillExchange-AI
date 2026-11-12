import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import apiService from '../../services/api';
import { BrainCircuit, Briefcase, ChevronRight, Compass, LineChart, Sparkles } from 'lucide-react';

const FreelancerDashboard = () => {
  const [recommendations, setRecommendations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchRecommendations = async () => {
      try {
        const data = await apiService.getRecommendations();
        setRecommendations(data);
      } catch (err) {
        console.error("Failed to load recommendations:", err);
        setError("Could not load AI recommendations. Make sure your local backend is running.");
      } finally {
        setLoading(false);
      }
    };
    fetchRecommendations();
  }, []);

  const getScoreColor = (score) => {
    if (score >= 80) return 'badge-green';
    if (score >= 50) return 'badge-purple';
    if (score >= 20) return 'badge-yellow';
    return 'badge-pink';
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Freelancer Dashboard</h1>
          <p className="page-subtitle">Personalized AI matchmaking based on your skill set.</p>
        </div>
        <button className="btn btn-primary" onClick={() => navigate('/freelancer/marketplace')}>
          <Compass size={18} /> Browse Marketplace
        </button>
      </div>

      {error && (
        <div style={{ background: 'rgba(239, 68, 68, 0.1)', border: '1px solid rgba(239, 68, 68, 0.3)', padding: '16px', borderRadius: 'var(--border-radius-md)', color: '#f87171', marginBottom: '24px' }}>
          {error}
        </div>
      )}

      {/* Stats Quickbar */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '20px', marginBottom: '40px' }}>
        <div className="card glass-panel metric-card" style={{ cursor: 'pointer' }} onClick={() => navigate('/freelancer/skills')}>
          <div className="metric-icon">
            <BrainCircuit size={24} />
          </div>
          <div>
            <div className="metric-label">AI Demand Index</div>
            <div className="metric-value">Market Match</div>
            <div style={{ fontSize: '0.75rem', color: 'var(--accent-purple)', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '4px', marginTop: '2px' }}>
              Analyze skills demand <ChevronRight size={12} />
            </div>
          </div>
        </div>

        <div className="card glass-panel metric-card" style={{ cursor: 'pointer' }} onClick={() => navigate('/freelancer/marketplace')}>
          <div className="metric-icon" style={{ color: 'var(--accent-green)', background: 'var(--accent-green-glow)' }}>
            <Briefcase size={24} />
          </div>
          <div>
            <div className="metric-label">Open Projects</div>
            <div className="metric-value">Explore</div>
            <div style={{ fontSize: '0.75rem', color: 'var(--accent-green)', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '4px', marginTop: '2px' }}>
              View all listings <ChevronRight size={12} />
            </div>
          </div>
        </div>

        <div className="card glass-panel metric-card" style={{ cursor: 'pointer' }} onClick={() => navigate('/chat')}>
          <div className="metric-icon" style={{ color: 'var(--accent-pink)', background: 'var(--accent-pink-glow)' }}>
            <Sparkles size={24} />
          </div>
          <div>
            <div className="metric-label">AI Communications</div>
            <div className="metric-value">Chat Panel</div>
            <div style={{ fontSize: '0.75rem', color: 'var(--accent-pink)', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '4px', marginTop: '2px' }}>
              Conversations overview <ChevronRight size={12} />
            </div>
          </div>
        </div>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '20px' }}>
        <Sparkles size={20} color="var(--accent-purple)" />
        <h2 style={{ fontSize: '1.25rem', fontWeight: 600 }}>Top AI Project Matches</h2>
      </div>

      {loading ? (
        <div style={{ display: 'flex', justifyContent: 'center', padding: '50px 0' }}>
          <div style={{ border: '3px solid var(--border-color)', borderTop: '3px solid var(--accent-purple)', borderRadius: '50%', width: '30px', height: '30px', animation: 'spin 1s linear infinite' }}></div>
        </div>
      ) : recommendations.length === 0 ? (
        <div className="card glass-panel" style={{ padding: '40px', textAlign: 'center', color: 'var(--text-secondary)' }}>
          <Briefcase size={40} style={{ margin: '0 auto 16px', opacity: 0.5 }} />
          <h3>No matching projects found</h3>
          <p style={{ fontSize: '0.9rem', marginTop: '8px' }}>Update your skills in Profile or browse the general project marketplace to find work.</p>
        </div>
      ) : (
        <div className="dashboard-grid">
          {recommendations.map((project) => (
            <div 
              key={project.id} 
              className="card glass-panel" 
              style={{ borderLeft: project.match_score >= 80 ? '3px solid var(--accent-green)' : project.match_score >= 50 ? '3px solid var(--accent-purple)' : '1px solid var(--border-color)' }}
            >
              <div className="card-header">
                <div>
                  <span className="badge badge-purple" style={{ marginBottom: '8px', fontSize: '0.65rem' }}>{project.category}</span>
                  <h3 className="card-title">{project.title}</h3>
                </div>
                <div style={{ textAlign: 'right' }}>
                  <span className={`badge ${getScoreColor(project.match_score)}`} style={{ display: 'block', fontSize: '0.75rem', padding: '6px 12px' }}>
                    {project.match_score}% Match
                  </span>
                </div>
              </div>

              <p className="card-desc">{project.description}</p>

              <div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '8px', fontWeight: 600 }}>Required Skills:</div>
                <div className="chip-container">
                  {project.required_skills ? (
                    project.required_skills.split(',').map((skill) => (
                      <span key={skill} className="chip" style={{ padding: '3px 8px', fontSize: '0.75rem' }}>
                        {skill.trim()}
                      </span>
                    ))
                  ) : (
                    <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>None</span>
                  )}
                </div>
              </div>

              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderTop: '1px solid var(--border-color)', paddingTop: '16px', marginTop: 'auto' }}>
                <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
                  By: <strong>{project.owner_name || 'Anonymous'}</strong>
                </span>
                <button 
                  className="btn btn-secondary btn-sm"
                  onClick={() => navigate(`/freelancer/projects/${project.id}`)}
                >
                  View details <ChevronRight size={14} />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default FreelancerDashboard;
