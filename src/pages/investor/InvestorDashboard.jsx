import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import apiService from '../../services/api';
import { 
  TrendingUp, 
  DollarSign, 
  Briefcase, 
  ShieldAlert,
  ChevronRight,
  TrendingDown,
  PieChart
} from 'lucide-react';

const InvestorDashboard = () => {
  const navigate = useNavigate();
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const statsData = await apiService.fundingStats();
        setStats(statsData);
      } catch (err) {
        console.error("Failed to load investor stats:", err);
        setError("Could not load investment stats. Ensure your local backend is running.");
      } finally {
        setLoading(false);
      }
    };
    fetchStats();
  }, []);

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Investor Analytics Dashboard</h1>
          <p className="page-subtitle">Track your tech investments, check valuations, and browse projects.</p>
        </div>
        <button className="btn btn-primary" onClick={() => navigate('/investor/marketplace')}>
          <Briefcase size={18} /> Invest in Projects
        </button>
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
      ) : stats ? (
        <div>
          {/* Key Metrics Grid */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '24px', marginBottom: '40px' }}>
            <div className="card glass-panel metric-card" style={{ borderLeft: '3px solid var(--accent-purple)' }}>
              <div className="metric-icon" style={{ background: 'var(--accent-purple-glow)', color: 'var(--accent-purple)' }}>
                <DollarSign size={24} />
              </div>
              <div>
                <div className="metric-label">Total Capital Invested</div>
                <div className="metric-value">${stats.total_invested.toLocaleString()}</div>
              </div>
            </div>

            <div className="card glass-panel metric-card" style={{ borderLeft: '3px solid var(--accent-green)' }}>
              <div className="metric-icon" style={{ background: 'var(--accent-green-glow)', color: 'var(--accent-green)' }}>
                <TrendingUp size={24} />
              </div>
              <div>
                <div className="metric-label">Simulated Portfolio Value</div>
                <div className="metric-value">${stats.portfolio_value.toLocaleString()}</div>
              </div>
            </div>

            <div className="card glass-panel metric-card" style={{ borderLeft: '3px solid var(--accent-pink)' }}>
              <div className="metric-icon" style={{ background: 'var(--accent-pink-glow)', color: 'var(--accent-pink)' }}>
                <PieChart size={24} />
              </div>
              <div>
                <div className="metric-label">Estimated Net ROI</div>
                <div className="metric-value" style={{ color: stats.roi_pct >= 0 ? '#10b981' : '#ef4444' }}>
                  +{stats.roi_pct}%
                </div>
              </div>
            </div>
          </div>

          {/* Secondary Stats Grid */}
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '30px' }}>
            <div className="card glass-panel" style={{ padding: '30px', cursor: 'pointer' }} onClick={() => navigate('/investor/portfolio')}>
              <h3 style={{ fontSize: '1.15rem', fontWeight: 600, marginBottom: '20px', display: 'flex', alignItems: 'center', gap: '8px' }}>
                <Briefcase size={20} color="var(--accent-purple)" /> Portfolio Composition
              </h3>
              
              <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid var(--border-color)', paddingBottom: '12px' }}>
                  <span style={{ color: 'var(--text-secondary)' }}>Total Investments Made:</span>
                  <span style={{ fontWeight: 600 }}>{stats.total_investments}</span>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid var(--border-color)', paddingBottom: '12px' }}>
                  <span style={{ color: 'var(--text-secondary)' }}>Active Projects Funded:</span>
                  <span style={{ fontWeight: 600 }}>{stats.active_investments}</span>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', paddingBottom: '4px' }}>
                  <span style={{ color: 'var(--text-secondary)' }}>Completed Exit Projects:</span>
                  <span style={{ fontWeight: 600 }}>{stats.completed_investments}</span>
                </div>
              </div>

              <div style={{ marginTop: '24px', display: 'flex', alignItems: 'center', gap: '4px', color: 'var(--accent-purple)', fontSize: '0.85rem', fontWeight: 600 }}>
                View complete ledger portfolio <ChevronRight size={14} />
              </div>
            </div>

            <div className="card glass-panel" style={{ padding: '30px' }}>
              <h3 style={{ fontSize: '1.15rem', fontWeight: 600, marginBottom: '16px', display: 'flex', alignItems: 'center', gap: '8px' }}>
                <TrendingUp size={20} color="var(--accent-green)" /> Investment Strategy Insights
              </h3>
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', lineHeight: '1.5', marginBottom: '16px' }}>
                Project exit returns are simulated at a **+25% multiplier** for completed milestones. Target teams that show a diversified skill matrix to decrease execution friction and lower execution risk.
              </p>
              <button 
                className="btn btn-secondary btn-sm" 
                style={{ width: '100%', marginTop: 'auto' }}
                onClick={() => navigate('/investor/marketplace')}
              >
                Explore High-Potential Teams
              </button>
            </div>
          </div>
        </div>
      ) : null}
    </div>
  );
};

export default InvestorDashboard;
