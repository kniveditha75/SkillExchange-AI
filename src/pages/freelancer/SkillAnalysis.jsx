import React, { useState, useEffect } from 'react';
import apiService from '../../services/api';
import { BrainCircuit, BookOpen, LineChart, TrendingUp, Sparkles, HelpCircle } from 'lucide-react';

const SkillAnalysis = () => {
  const [analysis, setAnalysis] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchAnalysis = async () => {
      try {
        const data = await apiService.getSkillAnalysis();
        setAnalysis(data);
      } catch (err) {
        console.error("Failed to load skill analysis:", err);
        setError("Unable to compute skill analysis insights.");
      } finally {
        setLoading(false);
      }
    };
    fetchAnalysis();
  }, []);

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', padding: '100px 0' }}>
        <div style={{ border: '3px solid var(--border-color)', borderTop: '3px solid var(--accent-purple)', borderRadius: '50%', width: '30px', height: '30px', animation: 'spin 1s linear infinite' }}></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="card glass-panel" style={{ padding: '30px', textAlign: 'center', color: '#f87171' }}>
        <p>{error}</p>
      </div>
    );
  }

  const getLevelBadge = (level) => {
    if (level === 'high') return 'badge-green';
    if (level === 'medium') return 'badge-purple';
    return 'badge-yellow';
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">AI Skill Market Analysis</h1>
          <p className="page-subtitle">Understand how your skills match current market demands.</p>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '30px', alignItems: 'start' }}>
        
        {/* Left column: Score Cards */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
          <div className="card glass-panel" style={{ padding: '30px', textAlign: 'center', position: 'relative', overflow: 'hidden' }}>
            <div style={{ position: 'absolute', top: '-10px', right: '-10px', opacity: 0.05, transform: 'rotate(15deg)' }}>
              <BrainCircuit size={150} />
            </div>

            <span style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--accent-purple)', textTransform: 'uppercase', letterSpacing: '0.05em', display: 'block', marginBottom: '8px' }}>
              Overall Market Alignment
            </span>
            <div style={{ fontSize: '4.5rem', fontWeight: 700, lineHeight: 1, background: 'var(--gradient-primary)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent', margin: '12px 0' }}>
              {analysis.overall_score}%
            </div>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.85rem' }}>
              Your alignment is computed based on the aggregate demand percentage of your skills across all analyzed projects.
            </p>
          </div>

          <div className="card glass-panel metric-card">
            <div className="metric-icon" style={{ background: 'var(--accent-green-glow)', color: 'var(--accent-green)' }}>
              <TrendingUp size={24} />
            </div>
            <div>
              <div className="metric-label">Analyzed Database</div>
              <div className="metric-value">{analysis.total_projects_analyzed} projects</div>
            </div>
          </div>
        </div>

        {/* Right column: Detailed breakdown */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '30px' }}>
          
          {/* User Skills Demand */}
          <div className="card glass-panel" style={{ padding: '30px' }}>
            <h3 style={{ fontSize: '1.2rem', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '20px' }}>
              <LineChart size={20} color="var(--accent-purple)" /> Your Skill Demand Breakdown
            </h3>

            {analysis.user_skills.length === 0 ? (
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>You have not added any skills to your profile yet.</p>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
                {analysis.user_skills.map((item) => (
                  <div key={item.skill} style={{ borderBottom: '1px solid var(--border-color)', paddingBottom: '16px' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                      <span style={{ fontWeight: 600, fontSize: '0.95rem' }}>{item.skill}</span>
                      <span className={`badge ${getLevelBadge(item.level)}`} style={{ fontSize: '0.65rem' }}>
                        {item.level} Demand
                      </span>
                    </div>
                    
                    <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                      <div style={{ flex: 1, height: '6px', background: 'var(--bg-tertiary)', borderRadius: '3px', overflow: 'hidden' }}>
                        <div 
                          style={{ 
                            width: `${item.demand_pct}%`, 
                            height: '100%', 
                            background: item.level === 'high' ? 'var(--accent-green)' : item.level === 'medium' ? 'var(--accent-purple)' : 'var(--accent-yellow)', 
                            borderRadius: '3px' 
                          }}
                        ></div>
                      </div>
                      <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', minWidth: '40px', textAlign: 'right' }}>
                        {item.demand_pct}%
                      </span>
                    </div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '4px' }}>
                      Required in {item.demand_count} active project(s).
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Suggested Skills to Learn */}
          <div className="card glass-panel" style={{ padding: '30px' }}>
            <h3 style={{ fontSize: '1.2rem', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '12px' }}>
              <BookOpen size={20} color="var(--accent-pink)" /> Recommended Skills to Expand
            </h3>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.85rem', marginBottom: '20px' }}>
              The following high-demand skills are missing from your profile. Adding these will qualify you for more match recommendation channels.
            </p>

            {analysis.suggested_skills.length === 0 ? (
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: 'var(--accent-green)', fontSize: '0.9rem' }}>
                <Sparkles size={16} /> All top demanded skills are already covered in your profile!
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                {analysis.suggested_skills.map((item) => (
                  <div 
                    key={item.skill} 
                    style={{ 
                      display: 'flex', 
                      justifyContent: 'space-between', 
                      alignItems: 'center', 
                      background: 'var(--bg-secondary)', 
                      padding: '12px 16px', 
                      borderRadius: 'var(--border-radius-sm)',
                      border: '1px solid var(--border-color)'
                    }}
                  >
                    <span style={{ fontWeight: 600, fontSize: '0.9rem' }}>{item.skill}</span>
                    <span className="badge badge-pink" style={{ fontSize: '0.65rem' }}>
                      In {item.demand_count} open project(s)
                    </span>
                  </div>
                ))}
              </div>
            )}
          </div>

        </div>

      </div>
    </div>
  );
};

export default SkillAnalysis;
