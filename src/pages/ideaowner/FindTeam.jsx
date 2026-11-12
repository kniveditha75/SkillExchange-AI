import React, { useState, useEffect } from 'react';
import apiService from '../../services/api';
import { Users, Check, X, ShieldAlert, Sparkles } from 'lucide-react';

const FindTeam = () => {
  const [myProjects, setMyProjects] = useState([]);
  const [selectedProjectId, setSelectedProjectId] = useState('');
  const [applicants, setApplicants] = useState([]);
  const [loadingProjects, setLoadingProjects] = useState(true);
  const [loadingApplicants, setLoadingApplicants] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchProjects = async () => {
      try {
        const data = await apiService.myProjects();
        setMyProjects(data);
        if (data.length > 0) {
          setSelectedProjectId(data[0].id.toString());
        }
      } catch (err) {
        console.error("Failed to load projects:", err);
        setError("Failed to load your projects list.");
      } finally {
        setLoadingProjects(false);
      }
    };
    fetchProjects();
  }, []);

  const fetchApplicants = async (projId) => {
    if (!projId) return;
    setLoadingApplicants(true);
    try {
      const data = await apiService.listApplicants(Number(projId));
      setApplicants(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoadingApplicants(false);
    }
  };

  useEffect(() => {
    if (selectedProjectId) {
      fetchApplicants(selectedProjectId);
    }
  }, [selectedProjectId]);

  const handleAction = async (applicantId, action) => {
    setSubmitting(true);
    try {
      await apiService.updateApplicant(Number(selectedProjectId), applicantId, { action });
      await fetchApplicants(selectedProjectId);
    } catch (err) {
      console.error(err);
      setError("Failed to process applicant decision.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Recruit Team Members</h1>
          <p className="page-subtitle">Review applications and accept collaborators into your project teams.</p>
        </div>
      </div>

      {error && (
        <div style={{ background: 'rgba(239, 68, 68, 0.1)', border: '1px solid rgba(239, 68, 68, 0.3)', padding: '16px', borderRadius: 'var(--border-radius-md)', color: '#f87171', marginBottom: '24px' }}>
          {error}
        </div>
      )}

      {loadingProjects ? (
        <div style={{ display: 'flex', justifyContent: 'center', padding: '30px 0' }}>
          <div style={{ border: '3px solid var(--border-color)', borderTop: '3px solid var(--accent-purple)', borderRadius: '50%', width: '24px', height: '24px', animation: 'spin 1s linear infinite' }}></div>
        </div>
      ) : myProjects.length === 0 ? (
        <div className="card glass-panel" style={{ padding: '40px', textAlign: 'center', color: 'var(--text-secondary)' }}>
          <ShieldAlert size={40} style={{ margin: '0 auto 16px', opacity: 0.5 }} />
          <h3>No projects created yet</h3>
          <p style={{ fontSize: '0.9rem', marginTop: '8px' }}>Create a project first before reviewing applicant lists.</p>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '30px' }}>
          
          <div className="card glass-panel" style={{ padding: '24px', display: 'flex', flexDirection: 'column', gap: '12px' }}>
            <label className="form-label" style={{ marginBottom: 0 }}>Select Project to Manage</label>
            <select 
              className="form-input" 
              value={selectedProjectId}
              onChange={(e) => setSelectedProjectId(e.target.value)}
              style={{ background: 'var(--bg-tertiary)', border: '1px solid var(--border-color)', height: '48px' }}
            >
              {myProjects.map(p => (
                <option key={p.id} value={p.id}>{p.title} ({p.category})</option>
              ))}
            </select>
          </div>

          <div className="card glass-panel" style={{ padding: '30px' }}>
            <h3 style={{ fontSize: '1.25rem', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '20px' }}>
              <Users size={20} color="var(--accent-purple)" /> Applicants List
            </h3>

            {loadingApplicants ? (
              <div style={{ display: 'flex', justifyContent: 'center', padding: '30px 0' }}>
                <div style={{ border: '3px solid var(--border-color)', borderTop: '3px solid var(--accent-purple)', borderRadius: '50%', width: '24px', height: '24px', animation: 'spin 1s linear infinite' }}></div>
              </div>
            ) : applicants.length === 0 ? (
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>No applications received for this project yet.</p>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                {applicants.map((app) => (
                  <div 
                    key={app.id} 
                    style={{ 
                      background: 'var(--bg-secondary)', 
                      border: '1px solid var(--border-color)', 
                      borderRadius: 'var(--border-radius-md)', 
                      padding: '20px',
                      opacity: app.status !== 'pending' ? 0.75 : 1
                    }}
                  >
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '12px' }}>
                      <div>
                        <h4 style={{ fontWeight: 600, fontSize: '1rem' }}>{app.applicant_name}</h4>
                        <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>{app.applicant_email}</span>
                      </div>
                      
                      {app.status === 'pending' ? (
                        <div style={{ display: 'flex', gap: '8px' }}>
                          <button 
                            className="btn btn-primary btn-sm"
                            style={{ background: 'var(--accent-green)', padding: '6px 12px' }}
                            onClick={() => handleAction(app.applicant_id, 'accept')}
                            disabled={submitting}
                          >
                            <Check size={14} /> Accept
                          </button>
                          <button 
                            className="btn btn-secondary btn-sm"
                            style={{ background: 'var(--accent-red)', color: 'white', border: 'none', padding: '6px 12px' }}
                            onClick={() => handleAction(app.applicant_id, 'reject')}
                            disabled={submitting}
                          >
                            <X size={14} /> Reject
                          </button>
                        </div>
                      ) : (
                        <span className={`badge ${app.status === 'accepted' ? 'badge-green' : 'badge-pink'}`}>
                          {app.status}
                        </span>
                      )}
                    </div>

                    <p style={{ color: 'var(--text-secondary)', fontSize: '0.85rem', marginBottom: '12px', background: 'var(--bg-tertiary)', padding: '10px', borderRadius: 'var(--border-radius-sm)', borderLeft: '3px solid var(--accent-purple)' }}>
                      "{app.message}"
                    </p>

                    <div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '6px', fontWeight: 600 }}>Skills:</div>
                      <div className="chip-container">
                        {app.applicant_skills.map((skill) => (
                          <span key={skill} className="chip" style={{ padding: '2px 6px', fontSize: '0.7rem' }}>
                            {skill}
                          </span>
                        ))}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

        </div>
      )}
    </div>
  );
};

export default FindTeam;
