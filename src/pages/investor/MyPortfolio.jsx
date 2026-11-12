import React, { useState, useEffect } from 'react';
import apiService from '../../services/api';
import { DollarSign, Briefcase, TrendingUp, Clock, CheckCircle2 } from 'lucide-react';

const MyPortfolio = () => {
  const [portfolio, setPortfolio] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchPortfolio = async () => {
      try {
        const data = await apiService.myPortfolio();
        setPortfolio(data);
      } catch (err) {
        console.error("Failed to load portfolio:", err);
        setError("Could not retrieve investment portfolio ledger.");
      } finally {
        setLoading(false);
      }
    };
    fetchPortfolio();
  }, []);

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">My Investment Portfolio</h1>
          <p className="page-subtitle">Historical log of financial backing and simulated asset valuations.</p>
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
      ) : portfolio.length === 0 ? (
        <div className="card glass-panel" style={{ padding: '50px', textAlign: 'center', color: 'var(--text-secondary)' }}>
          <DollarSign size={40} style={{ margin: '0 auto 16px', opacity: 0.5 }} />
          <h3>No investments recorded</h3>
          <p style={{ fontSize: '0.9rem', marginTop: '8px' }}>Explore the funding marketplace to back project teams.</p>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
          <div className="card glass-panel" style={{ padding: '0 24px', overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.9rem' }}>
              <thead>
                <tr style={{ borderBottom: '1px solid var(--border-color)' }}>
                  <th style={{ padding: '16px 12px', fontWeight: 600, color: 'var(--text-muted)' }}>Project Title</th>
                  <th style={{ padding: '16px 12px', fontWeight: 600, color: 'var(--text-muted)' }}>Date Funded</th>
                  <th style={{ padding: '16px 12px', fontWeight: 600, color: 'var(--text-muted)' }}>Amount Invested</th>
                  <th style={{ padding: '16px 12px', fontWeight: 600, color: 'var(--text-muted)' }}>Simulated Valuation</th>
                  <th style={{ padding: '16px 12px', fontWeight: 600, color: 'var(--text-muted)' }}>Project Status</th>
                  <th style={{ padding: '16px 12px', fontWeight: 600, color: 'var(--text-muted)' }}>Note</th>
                </tr>
              </thead>
              <tbody>
                {portfolio.map((item) => {
                  const simulatedValue = item.project_status === 'completed' ? item.amount * 1.25 : item.amount;
                  return (
                    <tr key={item.id} style={{ borderBottom: '1px solid var(--border-color)', height: '60px' }}>
                      <td style={{ padding: '12px', fontWeight: 600 }}>{item.project_title || 'Unknown Project'}</td>
                      <td style={{ padding: '12px', color: 'var(--text-secondary)' }}>
                        {new Date(item.created_at).toLocaleDateString()}
                      </td>
                      <td style={{ padding: '12px', fontWeight: 600 }}>${item.amount.toLocaleString()}</td>
                      <td style={{ padding: '12px', fontWeight: 600, color: item.project_status === 'completed' ? '#10b981' : 'var(--text-primary)' }}>
                        ${simulatedValue.toLocaleString()} 
                        {item.project_status === 'completed' && <span style={{ fontSize: '0.75rem', fontWeight: 500, marginLeft: '4px' }}>(+25%)</span>}
                      </td>
                      <td style={{ padding: '12px' }}>
                        <span className={`badge ${item.project_status === 'completed' ? 'badge-green' : item.project_status === 'open' ? 'badge-purple' : 'badge-yellow'}`} style={{ fontSize: '0.65rem' }}>
                          {item.project_status}
                        </span>
                      </td>
                      <td style={{ padding: '12px', color: 'var(--text-muted)', fontSize: '0.8rem', fontStyle: 'italic', maxWidth: '200px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                        {item.note || '—'}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
};

export default MyPortfolio;
