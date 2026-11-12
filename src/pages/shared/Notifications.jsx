import React, { useState, useEffect } from 'react';
import apiService from '../../services/api';
import { Bell, CheckCheck, MessageSquare, Briefcase, DollarSign, Check } from 'lucide-react';

const Notifications = () => {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchNotifications = async () => {
    try {
      const data = await apiService.listNotifications();
      setNotifications(data);
    } catch (err) {
      console.error("Failed to load notifications:", err);
      setError("Failed to fetch notifications list.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchNotifications();
  }, []);

  const handleMarkRead = async (id) => {
    try {
      await apiService.markRead(id);
      // Locally update
      setNotifications(prev => 
        prev.map(n => n.id === id ? { ...n, is_read: true } : n)
      );
    } catch (err) {
      console.error(err);
    }
  };

  const handleMarkAllRead = async () => {
    try {
      await apiService.markAllRead();
      setNotifications(prev => prev.map(n => ({ ...n, is_read: true })));
    } catch (err) {
      console.error(err);
    }
  };

  const getNotifIcon = (type) => {
    const style = { display: 'flex', alignItems: 'center', justifyContent: 'center', width: '36px', height: '36px', borderRadius: '50%' };
    if (type === 'message') {
      return (
        <div style={{ ...style, background: 'var(--accent-purple-glow)', color: 'var(--accent-purple)' }}>
          <MessageSquare size={16} />
        </div>
      );
    }
    if (type === 'investment') {
      return (
        <div style={{ ...style, background: 'var(--accent-green-glow)', color: 'var(--accent-green)' }}>
          <DollarSign size={16} />
        </div>
      );
    }
    return (
      <div style={{ ...style, background: 'var(--accent-pink-glow)', color: 'var(--accent-pink)' }}>
        <Briefcase size={16} />
      </div>
    );
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Workspace Notifications</h1>
          <p className="page-subtitle">Historical list of application requests, chat syncs, and backings.</p>
        </div>
        {notifications.some(n => !n.is_read) && (
          <button className="btn btn-secondary btn-sm" onClick={handleMarkAllRead}>
            <CheckCheck size={16} /> Mark all as read
          </button>
        )}
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
      ) : notifications.length === 0 ? (
        <div className="card glass-panel" style={{ padding: '50px', textAlign: 'center', color: 'var(--text-secondary)' }}>
          <Bell size={40} style={{ margin: '0 auto 16px', opacity: 0.5 }} />
          <h3>No notifications recorded</h3>
          <p style={{ fontSize: '0.9rem', marginTop: '8px' }}>Your notification feed is currently clean.</p>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
          {notifications.map((notif) => (
            <div 
              key={notif.id} 
              className="card glass-panel" 
              style={{ 
                padding: '16px 20px', 
                flexDirection: 'row', 
                alignItems: 'center', 
                gap: '16px',
                opacity: notif.is_read ? 0.65 : 1,
                borderLeft: notif.is_read ? '1px solid var(--border-color)' : '3px solid var(--accent-purple)'
              }}
            >
              {getNotifIcon(notif.notif_type)}

              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ fontWeight: 600, fontSize: '0.95rem' }}>{notif.title}</div>
                <div style={{ color: 'var(--text-secondary)', fontSize: '0.85rem', marginTop: '2px' }}>{notif.body}</div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '4px' }}>
                  {new Date(notif.created_at).toLocaleString()}
                </div>
              </div>

              {!notif.is_read && (
                <button 
                  onClick={() => handleMarkRead(notif.id)}
                  style={{ background: 'var(--bg-tertiary)', border: '1px solid var(--border-color)', width: '30px', height: '30px', borderRadius: '50%', display: 'flex', alignItems: 'center', justifycontent: 'center', display: 'flex', justifyContent: 'center', cursor: 'pointer', color: 'var(--text-secondary)' }}
                  title="Mark read"
                >
                  <Check size={16} />
                </button>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Notifications;
