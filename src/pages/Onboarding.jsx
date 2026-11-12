import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import apiService from '../services/api';
import { Code, BookOpen, Target, CheckCircle2, ChevronRight, ChevronLeft, Plus } from 'lucide-react';

const Onboarding = () => {
  const { user, refreshProfile } = useAuth();
  const navigate = useNavigate();
  const [step, setStep] = useState(1); // 1: Skills, 2: Interests, 3: Goals
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Selected state arrays
  const [selectedSkills, setSelectedSkills] = useState([]);
  const [selectedInterests, setSelectedInterests] = useState([]);
  const [selectedGoals, setSelectedGoals] = useState([]);

  // Custom text input
  const [customInput, setCustomInput] = useState('');

  // Pre-defined suggestions
  const suggestedSkills = [
    'Python', 'React', 'Kotlin', 'Android', 'Java', 'SQL', 'MySQL', 
    'Machine Learning', 'UI/UX Design', 'Node.js', 'FastAPI', 
    'Product Management', 'Data Analysis', 'Finance', 'Pitching'
  ];

  const suggestedInterests = [
    'Artificial Intelligence', 'Mobile Apps', 'Web Applications', 
    'FinTech', 'EdTech', 'HealthTech', 'Blockchain', 'Cybersecurity', 
    'Game Development', 'E-commerce', 'Social Networks', 'SaaS'
  ];

  const suggestedGoals = [
    'Build Portfolio', 'Join Startup', 'Find Co-founders', 
    'Learn New Technologies', 'Mentor Others', 'Build MVP', 
    'Secure Funding', 'Hire Talent', 'Invest in Tech Teams'
  ];

  const handleToggleItem = (item, type) => {
    if (type === 'skills') {
      setSelectedSkills(prev => 
        prev.includes(item) ? prev.filter(i => i !== item) : [...prev, item]
      );
    } else if (type === 'interests') {
      setSelectedInterests(prev => 
        prev.includes(item) ? prev.filter(i => i !== item) : [...prev, item]
      );
    } else if (type === 'goals') {
      setSelectedGoals(prev => 
        prev.includes(item) ? prev.filter(i => i !== item) : [...prev, item]
      );
    }
  };

  const handleAddCustom = (e, type) => {
    e.preventDefault();
    const val = customInput.trim();
    if (!val) return;

    if (type === 'skills' && !selectedSkills.includes(val)) {
      setSelectedSkills(prev => [...prev, val]);
    } else if (type === 'interests' && !selectedInterests.includes(val)) {
      setSelectedInterests(prev => [...prev, val]);
    } else if (type === 'goals' && !selectedGoals.includes(val)) {
      setSelectedGoals(prev => [...prev, val]);
    }
    setCustomInput('');
  };

  const handleNextStep = async () => {
    setError('');
    setLoading(true);

    try {
      if (step === 1) {
        if (selectedSkills.length === 0) {
          setError('Please select or input at least one skill.');
          setLoading(false);
          return;
        }
        await apiService.saveSkills({ skills: selectedSkills });
        setStep(2);
      } else if (step === 2) {
        if (selectedInterests.length === 0) {
          setError('Please select or input at least one interest.');
          setLoading(false);
          return;
        }
        await apiService.saveInterests({ interests: selectedInterests });
        setStep(3);
      } else if (step === 3) {
        if (selectedGoals.length === 0) {
          setError('Please select or input at least one goal.');
          setLoading(false);
          return;
        }
        await apiService.saveGoals({ goals: selectedGoals });
        
        // Onboarding complete! Refresh profile and navigate
        const profile = await refreshProfile();
        if (profile.role === 'freelancer') navigate('/freelancer/dashboard');
        else if (profile.role === 'idea_owner') navigate('/ideaowner/dashboard');
        else if (profile.role === 'investor') navigate('/investor/dashboard');
      }
    } catch (err) {
      console.error(err);
      setError('Failed to save details. Please check your network and try again.');
    } finally {
      setLoading(false);
    }
  };

  const handlePrevStep = () => {
    setError('');
    setStep(prev => Math.max(1, prev - 1));
  };

  return (
    <div className="auth-container">
      <div className="glass-panel" style={{ width: '100%', maxWidth: '700px', padding: '40px', borderRadius: 'var(--border-radius-lg)', boxShadow: 'var(--shadow-lg)' }}>
        <div className="onboarding-steps">
          <div className={`onboarding-step ${step === 1 ? 'active' : step > 1 ? 'completed' : ''}`}>
            {step > 1 ? <CheckCircle2 size={18} color="white" /> : 1}
          </div>
          <div className={`onboarding-step ${step === 2 ? 'active' : step > 2 ? 'completed' : ''}`}>
            {step > 2 ? <CheckCircle2 size={18} color="white" /> : 2}
          </div>
          <div className={`onboarding-step ${step === 3 ? 'active' : ''}`}>
            3
          </div>
        </div>

        {error && (
          <div style={{ background: 'rgba(239, 68, 68, 0.1)', border: '1px solid rgba(239, 68, 68, 0.3)', padding: '12px', borderRadius: 'var(--border-radius-sm)', color: '#f87171', fontSize: '0.85rem', marginBottom: '20px', textAlign: 'center' }}>
            {error}
          </div>
        )}

        {step === 1 && (
          <div>
            <div style={{ display: 'flex', gap: '12px', alignItems: 'center', marginBottom: '20px' }}>
              <div style={{ color: 'var(--accent-purple)' }}><Code size={28} /></div>
              <h2 style={{ fontSize: '1.5rem', fontWeight: 600 }}>What are your core skills?</h2>
            </div>
            <p style={{ color: 'var(--text-secondary)', marginBottom: '24px', fontSize: '0.95rem' }}>Select the technical capabilities or domain expertise you bring to the ecosystem.</p>

            <form onSubmit={(e) => handleAddCustom(e, 'skills')} style={{ display: 'flex', gap: '8px', marginBottom: '24px' }}>
              <input 
                type="text" 
                className="form-input" 
                placeholder="Enter custom skill..." 
                value={customInput}
                onChange={(e) => setCustomInput(e.target.value)}
              />
              <button type="submit" className="btn btn-secondary" style={{ padding: '0 16px' }}><Plus size={20} /></button>
            </form>

            <div className="chip-container" style={{ marginBottom: '30px' }}>
              {suggestedSkills.map(skill => (
                <span 
                  key={skill} 
                  className={`chip chip-interactive ${selectedSkills.includes(skill) ? 'chip-active' : ''}`}
                  onClick={() => handleToggleItem(skill, 'skills')}
                >
                  {skill}
                </span>
              ))}
            </div>

            {selectedSkills.length > 0 && (
              <div style={{ marginBottom: '20px' }}>
                <div style={{ fontSize: '0.85rem', fontWeight: 500, color: 'var(--text-muted)', marginBottom: '8px' }}>Selected ({selectedSkills.length}):</div>
                <div className="chip-container">
                  {selectedSkills.map(skill => (
                    <span 
                      key={skill} 
                      className="chip chip-active"
                      onClick={() => handleToggleItem(skill, 'skills')}
                      style={{ cursor: 'pointer' }}
                    >
                      {skill} &times;
                    </span>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}

        {step === 2 && (
          <div>
            <div style={{ display: 'flex', gap: '12px', alignItems: 'center', marginBottom: '20px' }}>
              <div style={{ color: 'var(--accent-pink)' }}><BookOpen size={28} /></div>
              <h2 style={{ fontSize: '1.5rem', fontWeight: 600 }}>What are your primary interests?</h2>
            </div>
            <p style={{ color: 'var(--text-secondary)', marginBottom: '24px', fontSize: '0.95rem' }}>Which startup domains, business categories, or project fields excite you?</p>

            <form onSubmit={(e) => handleAddCustom(e, 'interests')} style={{ display: 'flex', gap: '8px', marginBottom: '24px' }}>
              <input 
                type="text" 
                className="form-input" 
                placeholder="Enter custom interest..." 
                value={customInput}
                onChange={(e) => setCustomInput(e.target.value)}
              />
              <button type="submit" className="btn btn-secondary" style={{ padding: '0 16px' }}><Plus size={20} /></button>
            </form>

            <div className="chip-container" style={{ marginBottom: '30px' }}>
              {suggestedInterests.map(interest => (
                <span 
                  key={interest} 
                  className={`chip chip-interactive ${selectedInterests.includes(interest) ? 'chip-active' : ''}`}
                  onClick={() => handleToggleItem(interest, 'interests')}
                >
                  {interest}
                </span>
              ))}
            </div>

            {selectedInterests.length > 0 && (
              <div style={{ marginBottom: '20px' }}>
                <div style={{ fontSize: '0.85rem', fontWeight: 500, color: 'var(--text-muted)', marginBottom: '8px' }}>Selected ({selectedInterests.length}):</div>
                <div className="chip-container">
                  {selectedInterests.map(interest => (
                    <span 
                      key={interest} 
                      className="chip chip-active"
                      style={{ background: 'var(--accent-pink)', borderColor: 'var(--accent-pink)', cursor: 'pointer' }}
                      onClick={() => handleToggleItem(interest, 'interests')}
                    >
                      {interest} &times;
                    </span>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}

        {step === 3 && (
          <div>
            <div style={{ display: 'flex', gap: '12px', alignItems: 'center', marginBottom: '20px' }}>
              <div style={{ color: 'var(--accent-green)' }}><Target size={28} /></div>
              <h2 style={{ fontSize: '1.5rem', fontWeight: 600 }}>What are your goals?</h2>
            </div>
            <p style={{ color: 'var(--text-secondary)', marginBottom: '24px', fontSize: '0.95rem' }}>Select what you aim to achieve on the SkillExchange AI platform.</p>

            <form onSubmit={(e) => handleAddCustom(e, 'goals')} style={{ display: 'flex', gap: '8px', marginBottom: '24px' }}>
              <input 
                type="text" 
                className="form-input" 
                placeholder="Enter custom goal..." 
                value={customInput}
                onChange={(e) => setCustomInput(e.target.value)}
              />
              <button type="submit" className="btn btn-secondary" style={{ padding: '0 16px' }}><Plus size={20} /></button>
            </form>

            <div className="chip-container" style={{ marginBottom: '30px' }}>
              {suggestedGoals.map(goal => (
                <span 
                  key={goal} 
                  className={`chip chip-interactive ${selectedGoals.includes(goal) ? 'chip-active' : ''}`}
                  onClick={() => handleToggleItem(goal, 'goals')}
                >
                  {goal}
                </span>
              ))}
            </div>

            {selectedGoals.length > 0 && (
              <div style={{ marginBottom: '20px' }}>
                <div style={{ fontSize: '0.85rem', fontWeight: 500, color: 'var(--text-muted)', marginBottom: '8px' }}>Selected ({selectedGoals.length}):</div>
                <div className="chip-container">
                  {selectedGoals.map(goal => (
                    <span 
                      key={goal} 
                      className="chip chip-active"
                      style={{ background: 'var(--accent-green)', borderColor: 'var(--accent-green)', cursor: 'pointer' }}
                      onClick={() => handleToggleItem(goal, 'goals')}
                    >
                      {goal} &times;
                    </span>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}

        <div style={{ display: 'flex', justifyContent: 'space-between', borderTop: '1px solid var(--border-color)', paddingTop: '24px', marginTop: '20px' }}>
          {step > 1 ? (
            <button className="btn btn-secondary" onClick={handlePrevStep} disabled={loading}>
              <ChevronLeft size={16} /> Back
            </button>
          ) : (
            <div></div> // empty spacer
          )}
          
          <button className="btn btn-primary" onClick={handleNextStep} disabled={loading}>
            {loading ? 'Saving...' : step === 3 ? 'Finish Setup' : 'Next Step'} <ChevronRight size={16} />
          </button>
        </div>
      </div>
    </div>
  );
};

export default Onboarding;
