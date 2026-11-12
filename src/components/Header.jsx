import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import apiService from '../services/api';
import { Bell, MessageSquare, ShieldAlert } from 'lucide-react';

const Header = () => {
  const { user } = useAuth();
  const [unreadCount, setUnreadCount] = useState(0);
  const navigate = useNavigate();

  const getUnreadCount = async () => {
    try {
      if (user) {
        const countData = await apiService.unreadCount();
        setUnreadCount(countData.unread_count || 0);
      }
    } catch (error) {
      console.error("Failed to fetch notification count:", error);
    }
  };

  useEffect(() => {
    getUnreadCount();
    // Poll notifications count every 30 seconds
    const interval = setInterval(getUnreadCount, 30000);
    return () => clearInterval(interval);
  }, [user]);

  return (
    <header className="header">
      <div style={{ marginRight: 'auto', display: 'flex', gap: '12px', alignItems: 'center' }}>
        {user?.role && (
          <span className="badge badge-purple" style={{ fontSize: '0.8rem', padding: '6px 12px' }}>
            Active Session: {user.role === 'idea_owner' ? 'Idea Owner Mode' : user.role === 'investor' ? 'Investor Mode' : 'Freelancer Mode'}
          </span>
        )}
      </div>

      <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
        <button 
          className="header-icon-btn" 
          onClick={() => navigate('/chat')} 
          title="Conversations"
        >
          <MessageSquare size={18} />
        </button>

        <button 
          className="header-icon-btn" 
          onClick={() => navigate('/notifications')} 
          title="Notifications"
        >
          <Bell size={18} />
          {unreadCount > 0 && <span className="notification-dot"></span>}
        </button>

        <div 
          onClick={() => navigate('/profile')}
          style={{ cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '10px' }}
        >
          <div className="user-avatar" style={{ width: '34px', height: '34px' }}>
            {user?.profile_picture ? (
              <img 
                src={user.profile_picture.startsWith('http') ? user.profile_picture : `http://localhost:8000/${user.profile_picture}`} 
                alt={user?.full_name} 
                className="user-avatar"
                style={{ border: 'none', width: '34px', height: '34px' }}
              />
            ) : (
              user?.full_name?.charAt(0).toUpperCase() || 'U'
            )}
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;
