import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import apiService from '../../services/api';
import { Sparkles, Save, ArrowLeft, RefreshCw } from 'lucide-react';

const CreateProject = () => {
  const navigate = useNavigate();
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [category, setCategory] = useState('Web');
  const [requiredSkills, setRequiredSkills] = useState('');
  const [seekingFunding, setSeekingFunding] = useState(false);
  const [fundingGoal, setFundingGoal] = useState(10000);
  const [teamSize, setTeamSize] = useState(3);
  
  const [loading, setLoading] = useState(false);
  const [aiGenerating, setAiGenerating] = useState(false);
  const [error, setError] = useState('');
  const [aiMilestones, setAiMilestones] = useState([]);

  const handleCreate = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      await apiService.createProject({
        title,
        description,
        category,
        required_skills: requiredSkills,
        seeking_funding: seekingFunding,
        funding_goal: seekingFunding ? Number(fundingGoal) : 0,
        team_size: Number(teamSize)
      });
      navigate('/ideaowner/dashboard');
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.detail || 'Failed to create project.');
    } finally {
      setLoading(false);
    }
  };

  const handleGenerateIdea = async () => {
    if (!title) {
      setError('Please provide a project title/topic before generating with AI.');
      return;
    }

    setAiGenerating(true);
    setError('');
    try {
      const generated = await apiService.generateIdea(title, category);
      setDescription(generated.description);
      setAiMilestones(generated.suggested_milestones || []);
      
      // Auto recommend some skills based on category
      const skillMap = {
        'Web': 'HTML, CSS, JavaScript, React, Node.js, SQL',
        'Mobile': 'Kotlin, Android, Swift, Flutter, Mobile Design',
        'AI/ML': 'Python, PyTorch, TensorFlow, Scikit-learn, Pandas, Data Science',
        'Design': 'Figma, UI/UX Design, Adobe XD, Graphic Design, Prototyping',
        'Finance': 'Accounting, Financial Modeling, Venture Capital, Auditing',
      };
      setRequiredSkills(skillMap[category] || 'Communication, Project Management');
    } catch (err) {
      console.error("AI Idea Generation failed:", err);
      setError("AI Generation failed. Make sure the backend server is running.");
    } finally {
      setAiGenerating(false);
    }
  };

  return (
    <div>
      <button className="btn btn-secondary btn-sm" style={{ marginBottom: '24px' }} onClick={() => navigate('/ideaowner/dashboard')}>
        <ArrowLeft size={16} /> Cancel
      </button>

      <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '30px', alignItems: 'start' }}>
        
        {/* Main Creation Form */}
        <div className="card glass-panel" style={{ padding: '30px' }}>
          <h1 className="page-title" style={{ marginBottom: '20px' }}>Create New Project</h1>

          {error && (
            <div style={{ background: 'rgba(239, 68, 68, 0.1)', border: '1px solid rgba(239, 68, 68, 0.3)', padding: '12px', borderRadius: 'var(--border-radius-sm)', color: '#f87171', fontSize: '0.85rem', marginBottom: '20px' }}>
              {error}
            </div>
          )}

          <form onSubmit={handleCreate}>
            <div className="form-group">
              <label className="form-label">Project Title</label>
              <input 
                type="text" 
                className="form-input" 
                placeholder="e.g. Decentralized Freelance Market or FinTech Microloans"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                required
              />
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}>
              <div className="form-group">
                <label className="form-label">Category</label>
                <select 
                  className="form-input"
                  style={{ background: 'var(--bg-tertiary)', border: '1px solid var(--border-color)', height: '48px' }}
                  value={category}
                  onChange={(e) => setCategory(e.target.value)}
                >
                  <option value="Web">Web Apps</option>
                  <option value="Mobile">Mobile Apps</option>
                  <option value="AI/ML">AI & Machine Learning</option>
                  <option value="Design">Design & UI/UX</option>
                  <option value="Finance">Finance & Business</option>
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">Target Team Size (Includ. you)</label>
                <input 
                  type="number" 
                  className="form-input"
                  min="1"
                  max="50"
                  value={teamSize}
                  onChange={(e) => setTeamSize(e.target.value)}
                  required
                />
              </div>
            </div>

            {/* AI Generator button */}
            <div style={{ marginBottom: '24px' }}>
              <button 
                type="button" 
                className="btn btn-outline" 
                style={{ width: '100%', borderStyle: 'dashed', gap: '10px' }}
                onClick={handleGenerateIdea}
                disabled={aiGenerating}
              >
                {aiGenerating ? (
                  <>
                    <RefreshCw size={18} style={{ animation: 'spin 1.5s linear infinite' }} /> Generating Project Details...
                  </>
                ) : (
                  <>
                    <Sparkles size={18} color="var(--accent-purple)" /> Auto-Generate Details using AI Architect
                  </>
                )}
              </button>
              <style dangerouslySetInnerHTML={{__html: `
                @keyframes spin {
                  0% { transform: rotate(0deg); }
                  100% { transform: rotate(360deg); }
                }
              `}} />
            </div>

            <div className="form-group">
              <label className="form-label">Project Description</label>
              <textarea 
                className="form-input" 
                style={{ minHeight: '150px', resize: 'vertical' }}
                placeholder="Describe your vision, core features, and project scope..."
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label">Required Skills (Comma-separated)</label>
              <input 
                type="text" 
                className="form-input" 
                placeholder="e.g. Python, React, UI/UX, Docker"
                value={requiredSkills}
                onChange={(e) => setRequiredSkills(e.target.value)}
              />
              <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '4px', display: 'block' }}>
                Separate skills with commas. These are matched by the AI recommender against freelancer profiles.
              </span>
            </div>

            <div style={{ border: '1px solid var(--border-color)', borderRadius: 'var(--border-radius-md)', padding: '20px', marginBottom: '30px' }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: seekingFunding ? '16px' : '0' }}>
                <div>
                  <h4 style={{ fontWeight: 600, fontSize: '0.95rem' }}>Funding Registration</h4>
                  <p style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', marginTop: '2px' }}>Is this project open to financial investments?</p>
                </div>
                <input 
                  type="checkbox"
                  style={{ width: '20px', height: '20px', cursor: 'pointer', accentColor: 'var(--accent-purple)' }}
                  checked={seekingFunding}
                  onChange={(e) => setSeekingFunding(e.target.checked)}
                />
              </div>

              {seekingFunding && (
                <div className="form-group" style={{ marginBottom: 0 }}>
                  <label className="form-label">Funding Goal Amount ($)</label>
                  <input 
                    type="number" 
                    className="form-input"
                    value={fundingGoal}
                    onChange={(e) => setFundingGoal(e.target.value)}
                    min="100"
                    required
                  />
                </div>
              )}
            </div>

            <button type="submit" className="btn btn-primary" style={{ width: '100%', padding: '12px' }} disabled={loading}>
              <Save size={18} /> Launch Project
            </button>
          </form>
        </div>

        {/* Sidebar suggestions Panel */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
          {aiMilestones.length > 0 && (
            <div className="card glass-panel" style={{ padding: '24px' }}>
              <h3 style={{ fontSize: '1rem', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '16px' }}>
                <Sparkles size={16} color="var(--accent-purple)" /> AI Suggested Milestones
              </h3>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                {aiMilestones.map((milestone, idx) => (
                  <div key={idx} style={{ background: 'var(--bg-secondary)', border: '1px solid var(--border-color)', borderRadius: 'var(--border-radius-sm)', padding: '10px 14px' }}>
                    <div style={{ fontSize: '0.7rem', color: 'var(--accent-purple)', fontWeight: 600 }}>Milestone {idx + 1}</div>
                    <div style={{ fontWeight: 600, fontSize: '0.85rem', marginTop: '2px' }}>{milestone.name}</div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', marginTop: '4px' }}>{milestone.desc}</div>
                  </div>
                ))}
              </div>
            </div>
          )}

          <div className="card glass-panel" style={{ padding: '24px' }}>
            <h3 style={{ fontSize: '1rem', fontWeight: 600, marginBottom: '12px' }}>AI Match System</h3>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.85rem', lineHeight: '1.4' }}>
              When you specify required skills, the SkillExchange local match scoring engine calculates overlap against active freelancers to help you recruit.
            </p>
          </div>
        </div>

      </div>
    </div>
  );
};

export default CreateProject;
