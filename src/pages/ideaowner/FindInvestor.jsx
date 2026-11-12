import React, { useState, useEffect } from 'react';
import apiService from '../../services/api';
import { DollarSign, Briefcase, ChevronRight, TrendingUp } from 'lucide-react';

const FindInvestor = () => {
  const [fundingProjects, setFundingProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchFundingProjects = async () => {
      try {
        const data = await apiService.fundingProjects();
        setFundingProjects(data);
      } catch (err) {
        console.error("Failed to load funding projects:", err);
        setError("Could not load projects seeking funding.");
      } finally {
        setLoading(false);
      }
    };
    fetchFundingProjects();
  }, []);

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Funding Ecosystem Marketplace</h1>
          <p className="page-subtitle">Inspect active projects seeking investment and venture backing.</p>
        </div>
      </div>

      {error && (
        <div style={{ background: 'rgba(239, 68, 68, 0.1)', border: '1px solid rgba(239, 68, 68, 0.3)', padding: '16px', borderRadius: 'var(--border-radius-md)', color: '#f87171', marginBottom: '24px' }}>
          {error}
        </div>
      )}

      {loading ? (
        <div style={{ display: 'flex', justifyContent: 'center', padding: '50px 0' }}>
          <div style={{ border: '3px solid var(--border-color)', borderTop: '3px solid var(--accent-purple)', borderRadius: '50%', width: '30px', height: '30px', animation: 'spin 1s linear infinite' }}></div>
        </div>
      ) : fundingProjects.length === 0 ? (
        <div className="card glass-panel" style={{ padding: '50px', textAlign: 'center', color: 'var(--text-secondary)' }}>
          <Briefcase size={40} style={{ margin: '0 auto 16px', opacity: 0.5 }} />
          <h3>No projects seeking funding currently</h3>
          <p style={{ fontSize: '0.9rem', marginTop: '8px' }}>Toggle funding status on projects to list them in the marketplace.</p>
        </div>
      ) : (
        <div className="dashboard-grid">
          {fundingProjects.map((project) => {
            const fundingPercentage = Math.min(100, Math.round((project.amount_raised / project.funding_goal) * 100));
            return (
              <div key={project.id} className="card glass-panel">
                <div className="card-header">
                  <div>
                    <span className="badge badge-purple" style={{ marginBottom: '8px', fontSize: '0.65rem' }}>{project.category}</span>
                    <h3 className="card-title">{project.title}</h3>
                  </div>
                  <span className="badge badge-green" style={{ fontSize: '0.75rem', display: 'flex', alignItems: 'center', gap: '4px' }}>
                    <TrendingUp size={12} /> Active Goal
                  </span>
                </div>

                <p className="card-desc">{project.description}</p>

                <div style={{ background: 'var(--bg-secondary)', border: '1px solid var(--border-color)', padding: '16px', borderRadius: 'var(--border-radius-sm)', marginTop: 'auto' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.8rem', marginBottom: '8px' }}>
                    <span style={{ color: 'var(--text-secondary)' }}>Funding:</span>
                    <span style={{ fontWeight: 600 }}>${project.amount_raised.toLocaleString()} / ${project.funding_goal.toLocaleString()} ({fundingPercentage}%)</span>
                  </div>
                  <div style={{ width: '100%', height: '6px', background: 'var(--bg-tertiary)', borderRadius: '3px', overflow: 'hidden' }}>
                    <div style={{ width: `${fundingPercentage}%`, height: '100%', background: 'var(--gradient-green)', borderRadius: '3px' }}></div>
                  </div>
                </div>

                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderTop: '1px solid var(--border-color)', paddingTop: '16px', marginTop: '12px' }}>
                  <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
                    Recruits: <strong>{project.team_size} members</strong>
                  </span>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};

export default FindInvestor;
