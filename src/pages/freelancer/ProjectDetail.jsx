import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import apiService from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { ArrowLeft, Sparkles, Send, CheckCircle2, DollarSign, BrainCircuit, X } from 'lucide-react';

const ProjectDetail = () => {
  const { id } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [project, setProject] = useState(null);
  const [matchScore, setMatchScore] = useState(null);
  const [loading, setLoading] = useState(true);
  const [applyModalOpen, setApplyModalOpen] = useState(false);
  const [applicationMessage, setApplicationMessage] = useState('');
  const [applying, setApplying] = useState(false);
  const [applied, setApplied] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchProjectData = async () => {
      try {
        const projData = await apiService.getProject(id);
        setProject(projData);
        
        // Fetch AI match score
        const scoreData = await apiService.getMatchScore(id);
        setMatchScore(scoreData);
      } catch (err) {
        console.error("Error loading project detail:", err);
        setError("Failed to load project details.");
      } finally {
        setLoading(false);
      }
    };
    fetchProjectData();
  }, [id]);

  const handleApply = async (e) => {
    e.preventDefault();
    setApplying(true);
    setError('');
    try {
      await apiService.applyToProject(id, { message: applicationMessage });
      setApplied(true);
      setTimeout(() => {
        setApplyModalOpen(false);
      }, 2000);
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.detail || 'Failed to submit application.');
    } finally {
      setApplying(false);
    }
  };

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', padding: '100px 0' }}>
        <div style={{ border: '3px solid var(--border-color)', borderTop: '3px solid var(--accent-purple)', borderRadius: '50%', width: '30px', height: '30px', animation: 'spin 1s linear infinite' }}></div>
      </div>
    );
  }

  if (error && !project) {
    return (
      <div className="card glass-panel" style={{ padding: '30px', textAlign: 'center', color: '#f87171' }}>
        <p>{error}</p>
        <button className="btn btn-secondary btn-sm" style={{ marginTop: '16px' }} onClick={() => navigate(-1)}>
          Go Back
        </button>
      </div>
    );
  }

  const fundingPercentage = project.seeking_funding 
    ? Math.min(100, Math.round((project.amount_raised / project.funding_goal) * 100))
    : 0;

  return (
    <div>
      <button className="btn btn-secondary btn-sm" style={{ marginBottom: '24px' }} onClick={() => navigate(-1)}>
        <ArrowLeft size={16} /> Back
      </button>

      <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '30px', alignItems: 'start' }}>
        
        {/* Main Details Panel */}
        <div className="card glass-panel" style={{ padding: '30px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', borderBottom: '1px solid var(--border-color)', paddingBottom: '20px', marginBottom: '20px' }}>
            <div>
              <span className="badge badge-purple" style={{ marginBottom: '8px' }}>{project.category}</span>
              <h1 style={{ fontSize: '1.85rem', fontWeight: 700 }}>{project.title}</h1>
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', marginTop: '6px' }}>
                Posted by: <strong style={{ color: 'var(--text-primary)' }}>{project.owner_name || 'Anonymous'}</strong>
              </p>
            </div>
            
            {project.seeking_funding && (
              <span className="badge badge-green" style={{ fontSize: '0.8rem', padding: '6px 12px' }}>
                Funding Active
              </span>
            )}
          </div>

          <h3 style={{ fontSize: '1.1rem', fontWeight: 600, marginBottom: '12px' }}>Project Description</h3>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.95rem', lineHeight: '1.6', marginBottom: '30px', whiteSpace: 'pre-wrap' }}>
            {project.description}
          </p>

          <h3 style={{ fontSize: '1.1rem', fontWeight: 600, marginBottom: '12px' }}>Required Skills</h3>
          <div className="chip-container" style={{ marginBottom: '30px' }}>
            {project.required_skills ? (
              project.required_skills.split(',').map((skill) => (
                <span key={skill} className="chip" style={{ fontSize: '0.85rem', padding: '6px 12px' }}>
                  {skill.trim()}
                </span>
              ))
            ) : (
              <span style={{ color: 'var(--text-muted)' }}>None specified</span>
            )}
          </div>

          {project.seeking_funding && (
            <div style={{ background: 'var(--bg-secondary)', border: '1px solid var(--border-color)', padding: '20px', borderRadius: 'var(--border-radius-md)', marginBottom: '30px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <DollarSign size={20} color="var(--accent-green)" />
                  <span style={{ fontWeight: 600 }}>Funding Progress</span>
                </div>
                <span style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
                  ${project.amount_raised.toLocaleString()} / <strong>${project.funding_goal.toLocaleString()}</strong> ({fundingPercentage}%)
                </span>
              </div>
              <div style={{ width: '100%', height: '8px', background: 'var(--bg-tertiary)', borderRadius: '4px', overflow: 'hidden' }}>
                <div style={{ width: `${fundingPercentage}%`, height: '100%', background: 'var(--gradient-green)', borderRadius: '4px' }}></div>
              </div>
            </div>
          )}

          {/* Action buttons */}
          {user && user.id !== project.owner_id && (
            <div style={{ display: 'flex', gap: '12px' }}>
              <button 
                className="btn btn-primary" 
                style={{ padding: '12px 30px' }}
                onClick={() => setApplyModalOpen(true)}
              >
                Apply to Collaborate
              </button>
              <button 
                className="btn btn-secondary"
                onClick={async () => {
                  try {
                    const conv = await apiService.createConversation({ project_id: project.id, participant_id: project.owner_id });
                    navigate('/chat', { state: { conversationId: conv.id } });
                  } catch (err) {
                    console.error("Could not open chat with owner", err);
                  }
                }}
              >
                Chat with Owner
              </button>
            </div>
          )}
        </div>

        {/* Sidebar AI Match Analytics */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
          {matchScore && (
            <div className="card glass-panel" style={{ padding: '24px', textAlign: 'center' }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px', marginBottom: '16px', color: 'var(--accent-purple)' }}>
                <BrainCircuit size={20} />
                <span style={{ fontWeight: 600, textTransform: 'uppercase', fontSize: '0.75rem', letterSpacing: '0.05em' }}>AI Skill Match</span>
              </div>
              
              <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '16px' }}>
                <div className="radial-progress-container" style={{ width: '100px', height: '100px' }}>
                  <svg width="100" height="100" viewBox="0 0 36 36" style={{ transform: 'rotate(-90deg)' }}>
                    <path
                      d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                      fill="none"
                      stroke="var(--bg-tertiary)"
                      strokeWidth="3"
                    />
                    <path
                      d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                      fill="none"
                      stroke={matchScore.match_score >= 70 ? 'var(--accent-green)' : 'var(--accent-purple)'}
                      strokeWidth="3"
                      strokeDasharray={`${matchScore.match_score}, 100`}
                    />
                  </svg>
                  <div className="radial-progress-label" style={{ fontSize: '1.25rem' }}>
                    {matchScore.match_score}%
                  </div>
                </div>
              </div>
              
              <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', lineHeight: '1.4' }}>
                You possess <strong>{matchScore.user_skill_count}</strong> skill(s) matching <strong>{matchScore.project_skill_count}</strong> required skill(s) for this project.
              </p>
            </div>
          )}

          <div className="card glass-panel" style={{ padding: '24px' }}>
            <h3 style={{ fontSize: '1rem', fontWeight: 600, marginBottom: '16px' }}>Project Overview</h3>
            
            <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.85rem' }}>
                <span style={{ color: 'var(--text-secondary)' }}>Status:</span>
                <span className="badge badge-green" style={{ fontSize: '0.7rem' }}>{project.status}</span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.85rem' }}>
                <span style={{ color: 'var(--text-secondary)' }}>Team Size:</span>
                <span style={{ fontWeight: 600 }}>{project.team_size} members</span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.85rem' }}>
                <span style={{ color: 'var(--text-secondary)' }}>Created:</span>
                <span>{new Date(project.created_at).toLocaleDateString()}</span>
              </div>
            </div>
          </div>
        </div>

      </div>

      {/* Apply Modal */}
      {applyModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content glass-panel" style={{ maxWidth: '500px' }}>
            <div className="modal-close" onClick={() => setApplyModalOpen(false)}>
              <X size={20} />
            </div>

            <h2 style={{ fontSize: '1.35rem', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '8px' }}>
              <Sparkles size={20} color="var(--accent-purple)" /> Apply to Project
            </h2>

            {applied ? (
              <div style={{ textAlign: 'center', padding: '30px 0' }}>
                <CheckCircle2 size={48} color="var(--accent-green)" style={{ margin: '0 auto 16px' }} />
                <h3>Application Submitted!</h3>
                <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', marginTop: '8px' }}>Your collaboration request was sent to the idea owner.</p>
              </div>
            ) : (
              <form onSubmit={handleApply}>
                {error && (
                  <div style={{ background: 'rgba(239, 68, 68, 0.1)', border: '1px solid rgba(239, 68, 68, 0.3)', padding: '10px', borderRadius: 'var(--border-radius-sm)', color: '#f87171', fontSize: '0.85rem', marginBottom: '16px' }}>
                    {error}
                  </div>
                )}
                
                <div className="form-group">
                  <label className="form-label">Introduce yourself & explain your fit</label>
                  <textarea 
                    className="form-input" 
                    style={{ minHeight: '120px', resize: 'vertical' }}
                    placeholder="Hello! I would love to join your project. I have experience with..."
                    value={applicationMessage}
                    onChange={(e) => setApplicationMessage(e.target.value)}
                    required
                  />
                </div>

                <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px', marginTop: '10px' }}>
                  <button type="button" className="btn btn-secondary" onClick={() => setApplyModalOpen(false)}>
                    Cancel
                  </button>
                  <button type="submit" className="btn btn-primary" disabled={applying}>
                    {applying ? 'Submitting...' : 'Submit Application'}
                  </button>
                </div>
              </form>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default ProjectDetail;
