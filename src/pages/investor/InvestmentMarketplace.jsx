import React, { useState, useEffect } from 'react';
import apiService from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { 
  DollarSign, 
  Briefcase, 
  Sparkles, 
  TrendingUp, 
  Plus, 
  X, 
  AlertTriangle, 
  CheckCircle2, 
  Info
} from 'lucide-react';

const InvestmentMarketplace = () => {
  const { user } = useAuth();
  const [fundingProjects, setFundingProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [investModalOpen, setInvestModalOpen] = useState(false);
  const [selectedProject, setSelectedProject] = useState(null);
  
  // Invest Dialog state
  const [amount, setAmount] = useState('');
  const [note, setNote] = useState('');
  const [investing, setInvesting] = useState(false);
  const [invested, setInvested] = useState(false);
  const [error, setError] = useState('');

  // AI analysis cache
  const [aiAnalysisMap, setAiAnalysisMap] = useState({});
  const [analyzingId, setAnalyzingId] = useState(null);

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

  useEffect(() => {
    fetchFundingProjects();
  }, []);

  const handleFetchAnalysis = async (projectId) => {
    if (aiAnalysisMap[projectId] || analyzingId === projectId) return;
    setAnalyzingId(projectId);
    try {
      const analysis = await apiService.getProjectAnalysis(projectId);
      setAiAnalysisMap(prev => ({
        ...prev,
        [projectId]: analysis
      }));
    } catch (err) {
      console.error("Failed to fetch project analysis:", err);
    } finally {
      setAnalyzingId(null);
    }
  };

  const handleInvest = async (e) => {
    e.preventDefault();
    setInvesting(true);
    setError('');

    const remainingBudget = selectedProject.funding_goal - selectedProject.amount_raised;
    if (Number(amount) > remainingBudget) {
      setError(`Investment exceeds remaining budget. Maximum allowed: $${remainingBudget.toLocaleString()}`);
      setInvesting(false);
      return;
    }

    try {
      await apiService.invest(selectedProject.id, { 
        amount: Number(amount), 
        note: note 
      });
      setInvested(true);
      setTimeout(() => {
        setInvestModalOpen(false);
        setInvested(false);
        setAmount('');
        setNote('');
        fetchFundingProjects(); // Reload list
      }, 2000);
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.detail || 'Failed to submit investment.');
    } finally {
      setInvesting(false);
    }
  };

  const getRiskColor = (risk) => {
    if (risk === 'Low') return 'badge-green';
    if (risk === 'Medium') return 'badge-yellow';
    return 'badge-pink';
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Funding Marketplace</h1>
          <p className="page-subtitle">Examine startup metrics, audit risk with AI, and back founders.</p>
        </div>
      </div>

      {loading ? (
        <div style={{ display: 'flex', justifyContent: 'center', padding: '50px 0' }}>
          <div style={{ border: '3px solid var(--border-color)', borderTop: '3px solid var(--accent-purple)', borderRadius: '50%', width: '30px', height: '30px', animation: 'spin 1s linear infinite' }}></div>
        </div>
      ) : fundingProjects.length === 0 ? (
        <div className="card glass-panel" style={{ padding: '50px', textAlign: 'center', color: 'var(--text-secondary)' }}>
          <Briefcase size={40} style={{ margin: '0 auto 16px', opacity: 0.5 }} />
          <h3>No projects seeking funding currently</h3>
          <p style={{ fontSize: '0.9rem', marginTop: '8px' }}>Check back later once founders upload budgets.</p>
        </div>
      ) : (
        <div className="dashboard-grid">
          {fundingProjects.map((project) => {
            const fundingPercentage = Math.min(100, Math.round((project.amount_raised / project.funding_goal) * 100));
            const analysis = aiAnalysisMap[project.id];
            
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
                  <div style={{ display: 'flex', justifycontent: 'space-between', display: 'flex', justifyContent: 'space-between', fontSize: '0.8rem', marginBottom: '8px' }}>
                    <span style={{ color: 'var(--text-secondary)' }}>Progress:</span>
                    <span style={{ fontWeight: 600 }}>${project.amount_raised.toLocaleString()} / ${project.funding_goal.toLocaleString()} ({fundingPercentage}%)</span>
                  </div>
                  <div style={{ width: '100%', height: '6px', background: 'var(--bg-tertiary)', borderRadius: '3px', overflow: 'hidden' }}>
                    <div style={{ width: `${fundingPercentage}%`, height: '100%', background: 'var(--gradient-green)', borderRadius: '3px' }}></div>
                  </div>
                </div>

                {/* AI Auditing Drawer */}
                {analysis ? (
                  <div style={{ background: 'rgba(99, 102, 241, 0.04)', border: '1px solid var(--accent-purple-glow)', padding: '12px', borderRadius: 'var(--border-radius-sm)', marginTop: '12px', fontSize: '0.8rem' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                      <span style={{ fontWeight: 600, color: 'var(--accent-purple)', display: 'flex', alignItems: 'center', gap: '4px' }}>
                        <Sparkles size={12} /> AI Health Analysis
                      </span>
                      <span className={`badge ${getRiskColor(analysis.risk_level)}`} style={{ fontSize: '0.6rem', padding: '2px 6px' }}>
                        {analysis.risk_level} Risk ({analysis.health_score}%)
                      </span>
                    </div>
                    <ul style={{ paddingLeft: '14px', color: 'var(--text-secondary)' }}>
                      {analysis.insights.map((insight, idx) => (
                        <li key={idx} style={{ marginTop: '2px' }}>{insight}</li>
                      ))}
                    </ul>
                  </div>
                ) : (
                  <button 
                    className="btn btn-secondary btn-sm"
                    style={{ marginTop: '12px', width: '100%', padding: '6px', fontSize: '0.75rem', gap: '4px' }}
                    onClick={() => handleFetchAnalysis(project.id)}
                    disabled={analyzingId === project.id}
                  >
                    <Sparkles size={12} color="var(--accent-purple)" />
                    {analyzingId === project.id ? 'Auditing metrics...' : 'Audit project with AI Analytics'}
                  </button>
                )}

                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderTop: '1px solid var(--border-color)', paddingTop: '16px', marginTop: '16px' }}>
                  <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
                    Recruits: <strong>{project.team_size} members</strong>
                  </span>
                  <button 
                    className="btn btn-primary btn-sm"
                    onClick={() => {
                      setSelectedProject(project);
                      setInvestModalOpen(true);
                    }}
                    disabled={project.amount_raised >= project.funding_goal}
                  >
                    {project.amount_raised >= project.funding_goal ? 'Goal Met' : 'Back Project'}
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Invest Modal */}
      {investModalOpen && selectedProject && (
        <div className="modal-overlay">
          <div className="modal-content glass-panel" style={{ maxWidth: '480px' }}>
            <div className="modal-close" onClick={() => setInvestModalOpen(false)}>
              <X size={20} />
            </div>

            <h2 style={{ fontSize: '1.35rem', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '8px' }}>
              <DollarSign size={22} color="var(--accent-green)" /> Back Startup Idea
            </h2>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.85rem' }}>
              You are investing in <strong style={{ color: 'var(--text-primary)' }}>{selectedProject.title}</strong> by {selectedProject.owner_name}.
            </p>

            {invested ? (
              <div style={{ textAlign: 'center', padding: '30px 0' }}>
                <CheckCircle2 size={48} color="var(--accent-green)" style={{ margin: '0 auto 16px' }} />
                <h3>Investment Registered!</h3>
                <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', marginTop: '8px' }}>Your financial backing was wired to the project ledger.</p>
              </div>
            ) : (
              <form onSubmit={handleInvest}>
                {error && (
                  <div style={{ background: 'rgba(239, 68, 68, 0.1)', border: '1px solid rgba(239, 68, 68, 0.3)', padding: '10px', borderRadius: 'var(--border-radius-sm)', color: '#f87171', fontSize: '0.85rem', marginBottom: '16px' }}>
                    {error}
                  </div>
                )}
                
                <div className="form-group">
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
                    <label className="form-label" style={{ marginBottom: 0 }}>Investment Amount ($)</label>
                    <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                      Max allowed: ${(selectedProject.funding_goal - selectedProject.amount_raised).toLocaleString()}
                    </span>
                  </div>
                  <input 
                    type="number" 
                    className="form-input" 
                    placeholder="Enter amount (e.g. 500)"
                    value={amount}
                    onChange={(e) => setAmount(e.target.value)}
                    min="1"
                    required
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Note to Founders (Optional)</label>
                  <input 
                    type="text" 
                    className="form-input" 
                    placeholder="e.g. Excited to back your team! Keep up the good work."
                    value={note}
                    onChange={(e) => setNote(e.target.value)}
                  />
                </div>

                <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px', marginTop: '10px' }}>
                  <button type="button" className="btn btn-secondary" onClick={() => setInvestModalOpen(false)}>
                    Cancel
                  </button>
                  <button type="submit" className="btn btn-primary" disabled={investing}>
                    {investing ? 'Transacting...' : 'Wire Investment'}
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

export default InvestmentMarketplace;
