import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import apiService from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { Briefcase, MessageSquare, Clock, CheckCircle2, XCircle } from 'lucide-react';

const MyWork = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('memberships'); // memberships / applications
  const [memberships, setMemberships] = useState([]);
  const [appliedProjects, setAppliedProjects] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchData = async () => {
    setLoading(true);
    try {
      // 1. Fetch memberships (projects we are a member of)
      const memberData = await apiService.myProjects();
      setMemberships(memberData);

      // 2. Fetch all projects to find ones we applied to
      const allProjects = await apiService.listProjects();
      const applied = allProjects.filter(p => p.application_status !== null);
      setAppliedProjects(applied);
    } catch (error) {
      console.error("Failed to load work data:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const getStatusBadge = (status) => {
    if (status === 'accepted') {
      return (
        <span className="badge badge-green" style={{ display: 'inline-flex', alignItems: 'center', gap: '4px' }}>
          <CheckCircle2 size={12} /> Accepted
        </span>
      );
    }
    if (status === 'rejected') {
      return (
        <span className="badge badge-pink" style={{ display: 'inline-flex', alignItems: 'center', gap: '4px' }}>
          <XCircle size={12} /> Rejected
        </span>
      );
    }
    return (
      <span className="badge badge-yellow" style={{ display: 'inline-flex', alignItems: 'center', gap: '4px' }}>
        <Clock size={12} /> Pending
      </span>
    );
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">My Applications & Collaborations</h1>
          <p className="page-subtitle">Track your project involvements and application statuses.</p>
        </div>
      </div>

      <div className="tabs-header" style={{ marginBottom: '30px' }}>
        <button 
          className={`tab-btn ${activeTab === 'memberships' ? 'active' : ''}`}
          onClick={() => setActiveTab('memberships')}
        >
          Ongoing Teams ({memberships.length})
        </button>
        <button 
          className={`tab-btn ${activeTab === 'applications' ? 'active' : ''}`}
          onClick={() => setActiveTab('applications')}
        >
          Sent Applications ({appliedProjects.length})
        </button>
      </div>

      {loading ? (
        <div style={{ display: 'flex', justifyContent: 'center', padding: '50px 0' }}>
          <div style={{ border: '3px solid var(--border-color)', borderTop: '3px solid var(--accent-purple)', borderRadius: '50%', width: '30px', height: '30px', animation: 'spin 1s linear infinite' }}></div>
        </div>
      ) : activeTab === 'memberships' ? (
        memberships.length === 0 ? (
          <div className="card glass-panel" style={{ padding: '50px', textAlign: 'center', color: 'var(--text-secondary)' }}>
            <Briefcase size={40} style={{ margin: '0 auto 16px', opacity: 0.5 }} />
            <h3>You aren't in any project team yet</h3>
            <p style={{ fontSize: '0.9rem', marginTop: '8px' }}>Apply to projects in the marketplace. Once the owner accepts you, they will appear here.</p>
          </div>
        ) : (
          <div className="dashboard-grid">
            {memberships.map((project) => (
              <div key={project.id} className="card glass-panel">
                <div className="card-header">
                  <div>
                    <span className="badge badge-purple" style={{ marginBottom: '8px', fontSize: '0.65rem' }}>{project.category}</span>
                    <h3 className="card-title">{project.title}</h3>
                  </div>
                  <span className="badge badge-green" style={{ fontSize: '0.75rem' }}>Active Member</span>
                </div>

                <p className="card-desc">{project.description}</p>

                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderTop: '1px solid var(--border-color)', paddingTop: '16px', marginTop: 'auto' }}>
                  <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
                    Owner: <strong>{project.owner_name}</strong>
                  </span>
                  <div style={{ display: 'flex', gap: '8px' }}>
                    <button 
                      className="btn btn-secondary btn-sm"
                      onClick={async () => {
                        try {
                          const conv = await apiService.createConversation({ project_id: project.id, participant_id: project.owner_id });
                          navigate('/chat', { state: { conversationId: conv.id } });
                        } catch (err) {
                          console.error("Could not open chat with owner", err);
                        }
                      }}
                      title="Project Chat"
                    >
                      <MessageSquare size={14} />
                    </button>
                    <button 
                      className="btn btn-primary btn-sm"
                      onClick={() => navigate(`/freelancer/projects/${project.id}`)}
                    >
                      Workspace
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )
      ) : (
        appliedProjects.length === 0 ? (
          <div className="card glass-panel" style={{ padding: '50px', textAlign: 'center', color: 'var(--text-secondary)' }}>
            <Briefcase size={40} style={{ margin: '0 auto 16px', opacity: 0.5 }} />
            <h3>No pending applications</h3>
            <p style={{ fontSize: '0.9rem', marginTop: '8px' }}>Browse the marketplace to find projects to apply to.</p>
          </div>
        ) : (
          <div className="dashboard-grid">
            {appliedProjects.map((project) => (
              <div key={project.id} className="card glass-panel">
                <div className="card-header">
                  <div>
                    <span className="badge badge-purple" style={{ marginBottom: '8px', fontSize: '0.65rem' }}>{project.category}</span>
                    <h3 className="card-title">{project.title}</h3>
                  </div>
                  {getStatusBadge(project.application_status)}
                </div>

                <p className="card-desc">{project.description}</p>

                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderTop: '1px solid var(--border-color)', paddingTop: '16px', marginTop: 'auto' }}>
                  <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
                    Owner: <strong>{project.owner_name}</strong>
                  </span>
                  <button 
                    className="btn btn-secondary btn-sm"
                    onClick={() => navigate(`/freelancer/projects/${project.id}`)}
                  >
                    View Details
                  </button>
                </div>
              </div>
            ))}
          </div>
        )
      )}
    </div>
  );
};

export default MyWork;
