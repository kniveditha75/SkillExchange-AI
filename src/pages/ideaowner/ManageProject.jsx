import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import apiService from '../../services/api';
import { 
  ArrowLeft, 
  Users, 
  Check, 
  X, 
  Trash2, 
  Sparkles, 
  TrendingUp, 
  AlertTriangle,
  CheckCircle2, 
  MessageSquare
} from 'lucide-react';

const ManageProject = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [project, setProject] = useState(null);
  const [applicants, setApplicants] = useState([]);
  const [members, setMembers] = useState([]);
  const [healthAnalysis, setHealthAnalysis] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  const fetchProjectData = async () => {
    try {
      const proj = await apiService.getProject(id);
      setProject(proj);
      
      const apps = await apiService.listApplicants(id);
      setApplicants(apps);
      
      const mems = await apiService.listProjectMembers(id);
      setMembers(mems);

      const health = await apiService.getProjectAnalysis(id);
      setHealthAnalysis(health);
    } catch (err) {
      console.error("Failed to load management details:", err);
      setError("Failed to fetch project details.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProjectData();
  }, [id]);

  const handleApplicantAction = async (appId, applicantId, action) => {
    setSubmitting(true);
    try {
      await apiService.updateApplicant(id, applicantId, { action });
      await fetchProjectData(); // Refresh datasets
    } catch (err) {
      console.error(err);
      setError("Failed to process applicant decision.");
    } finally {
      setSubmitting(false);
    }
  };

  const handleRemoveMember = async (userId) => {
    if (!window.confirm("Are you sure you want to remove this member from the project?")) {
      return;
    }
    setSubmitting(true);
    try {
      await apiService.removeProjectMember(id, userId);
      await fetchProjectData();
    } catch (err) {
      console.error(err);
      setError("Failed to remove member.");
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', padding: '100px 0' }}>
        <div style={{ border: '3px solid var(--border-color)', borderTop: '3px solid var(--accent-purple)', borderRadius: '50%', width: '30px', height: '30px', animation: 'spin 1s linear infinite' }}></div>
      </div>
    );
  }

  const getRiskColor = (risk) => {
    if (risk === 'Low') return 'badge-green';
    if (risk === 'Medium') return 'badge-yellow';
    return 'badge-pink';
  };

  return (
    <div>
      <button className="btn btn-secondary btn-sm" style={{ marginBottom: '24px' }} onClick={() => navigate('/ideaowner/dashboard')}>
        <ArrowLeft size={16} /> Back to Dashboard
      </button>

      {error && (
        <div style={{ background: 'rgba(239, 68, 68, 0.1)', border: '1px solid rgba(239, 68, 68, 0.3)', padding: '16px', borderRadius: 'var(--border-radius-md)', color: '#f87171', marginBottom: '24px' }}>
          {error}
        </div>
      )}

      {project && (
        <div style={{ marginBottom: '30px' }}>
          <span className="badge badge-purple" style={{ marginBottom: '8px' }}>{project.category}</span>
          <h1 className="page-title">{project.title}</h1>
          <p className="page-subtitle">Project Management Workspace</p>
        </div>
      )}

      <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '30px', alignItems: 'start' }}>
        
        {/* Left Hand: Applicants & Current Team */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '30px' }}>
          
          {/* Applications list */}
          <div className="card glass-panel" style={{ padding: '30px' }}>
            <h3 style={{ fontSize: '1.2rem', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '20px' }}>
              <Users size={20} color="var(--accent-purple)" /> Pending Applications ({applicants.filter(a => a.status === 'pending').length})
            </h3>

            {applicants.filter(a => a.status === 'pending').length === 0 ? (
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>No pending applications for this project.</p>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                {applicants.filter(a => a.status === 'pending').map((app) => (
                  <div key={app.id} style={{ background: 'var(--bg-secondary)', border: '1px solid var(--border-color)', borderRadius: 'var(--border-radius-md)', padding: '20px' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '12px' }}>
                      <div>
                        <h4 style={{ fontWeight: 600, fontSize: '1rem' }}>{app.applicant_name}</h4>
                        <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>{app.applicant_email}</span>
                      </div>
                      <div style={{ display: 'flex', gap: '8px' }}>
                        <button 
                          className="btn btn-primary btn-sm"
                          style={{ background: 'var(--accent-green)', padding: '6px 10px' }}
                          onClick={() => handleApplicantAction(app.id, app.applicant_id, 'accept')}
                          disabled={submitting}
                          title="Accept Applicant"
                        >
                          <Check size={16} /> Accept
                        </button>
                        <button 
                          className="btn btn-secondary btn-sm"
                          style={{ background: 'var(--accent-red)', color: 'white', border: 'none', padding: '6px 10px' }}
                          onClick={() => handleApplicantAction(app.id, app.applicant_id, 'reject')}
                          disabled={submitting}
                          title="Reject Applicant"
                        >
                          <X size={16} /> Reject
                        </button>
                      </div>
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

          {/* Members list */}
          <div className="card glass-panel" style={{ padding: '30px' }}>
            <h3 style={{ fontSize: '1.2rem', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '20px' }}>
              <Users size={20} color="var(--accent-green)" /> Project Members ({members.length})
            </h3>

            {members.length === 0 ? (
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>No team members recruited yet. Use recruitment matching to find talent!</p>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                {members.map((member) => (
                  <div 
                    key={member.user_id} 
                    style={{ 
                      display: 'flex', 
                      justifyContent: 'space-between', 
                      alignItems: 'center', 
                      background: 'var(--bg-secondary)', 
                      padding: '12px 20px', 
                      borderRadius: 'var(--border-radius-sm)',
                      border: '1px solid var(--border-color)'
                    }}
                  >
                    <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                      <div className="user-avatar" style={{ width: '32px', height: '32px', fontSize: '0.8rem' }}>
                        {member.profile_picture ? (
                          <img 
                            src={member.profile_picture.startsWith('http') ? member.profile_picture : `http://localhost:8000/${member.profile_picture}`} 
                            alt={member.full_name} 
                            style={{ width: '32px', height: '32px', borderRadius: '50%' }}
                          />
                        ) : (
                          member.full_name.charAt(0).toUpperCase()
                        )}
                      </div>
                      <div>
                        <div style={{ fontWeight: 600, fontSize: '0.9rem' }}>{member.full_name}</div>
                        <div style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>{member.email}</div>
                      </div>
                    </div>

                    <div style={{ display: 'flex', gap: '8px' }}>
                      <button 
                        className="btn btn-secondary btn-sm"
                        onClick={async () => {
                          try {
                            const conv = await apiService.createConversation({ project_id: Number(id), participant_id: member.user_id });
                            navigate('/chat', { state: { conversationId: conv.id } });
                          } catch (err) {
                            console.error(err);
                          }
                        }}
                        title="Direct Message"
                      >
                        <MessageSquare size={14} />
                      </button>
                      <button 
                        className="btn btn-secondary btn-sm"
                        style={{ color: 'var(--accent-red)' }}
                        onClick={() => handleRemoveMember(member.user_id)}
                        disabled={submitting}
                        title="Remove Member"
                      >
                        <Trash2 size={14} />
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

        </div>

        {/* Right Hand: AI Analytics & Health Status */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
          {healthAnalysis && (
            <div className="card glass-panel" style={{ padding: '24px', textAlign: 'center' }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px', marginBottom: '16px', color: 'var(--accent-purple)' }}>
                <Sparkles size={18} />
                <span style={{ fontWeight: 600, textTransform: 'uppercase', fontSize: '0.75rem', letterSpacing: '0.05em' }}>AI Health Score</span>
              </div>

              <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '16px' }}>
                <div className="radial-progress-container" style={{ width: '90px', height: '90px' }}>
                  <svg width="90" height="90" viewBox="0 0 36 36" style={{ transform: 'rotate(-90deg)' }}>
                    <path
                      d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                      fill="none"
                      stroke="var(--bg-tertiary)"
                      strokeWidth="3.5"
                    />
                    <path
                      d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                      fill="none"
                      stroke={healthAnalysis.health_score >= 80 ? 'var(--accent-green)' : healthAnalysis.health_score >= 50 ? 'var(--accent-yellow)' : 'var(--accent-pink)'}
                      strokeWidth="3.5"
                      strokeDasharray={`${healthAnalysis.health_score}, 100`}
                    />
                  </svg>
                  <div className="radial-progress-label" style={{ fontSize: '1.2rem' }}>
                    {healthAnalysis.health_score}%
                  </div>
                </div>
              </div>

              <div style={{ marginBottom: '16px' }}>
                <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>Risk Level: </span>
                <span className={`badge ${getRiskColor(healthAnalysis.risk_level)}`} style={{ fontSize: '0.7rem' }}>
                  {healthAnalysis.risk_level} Risk
                </span>
              </div>

              <div style={{ textAlign: 'left', borderTop: '1px solid var(--border-color)', paddingTop: '16px' }}>
                <div style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-muted)', marginBottom: '8px' }}>AI Insights:</div>
                <ul style={{ paddingLeft: '16px', fontSize: '0.8rem', color: 'var(--text-secondary)', display: 'flex', flexDirection: 'column', gap: '6px' }}>
                  {healthAnalysis.insights.map((insight, idx) => (
                    <li key={idx}>{insight}</li>
                  ))}
                  {healthAnalysis.insights.length === 0 && (
                    <li>No risk markers detected. Project execution looks stable.</li>
                  )}
                </ul>
              </div>
            </div>
          )}

          <div className="card glass-panel" style={{ padding: '24px' }}>
            <h3 style={{ fontSize: '1rem', fontWeight: 600, marginBottom: '12px' }}>Recruitment Advice</h3>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.85rem', lineHeight: '1.4' }}>
              Ensure your team meets the target membership limits. You can look up matches via the team search router.
            </p>
          </div>
        </div>

      </div>
    </div>
  );
};

export default ManageProject;
