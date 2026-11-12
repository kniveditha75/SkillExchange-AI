import React, { useState, useEffect } from 'react';
import apiService from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { User, Phone, Check, RefreshCw, Upload, Sparkles, Star } from 'lucide-react';

const Profile = () => {
  const { user, refreshProfile, updateProfileState } = useAuth();
  
  // Basic Info Form states
  const [fullName, setFullName] = useState('');
  const [gender, setGender] = useState('');
  const [phone, setPhone] = useState('');
  
  // Editing states
  const [updating, setUpdating] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [roleSwitching, setRoleSwitching] = useState(false);
  
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [ratings, setRatings] = useState([]);
  const [ratingStats, setRatingStats] = useState(null);

  useEffect(() => {
    if (user) {
      setFullName(user.full_name || '');
      setGender(user.gender || 'Male');
      setPhone(user.phone || '');
      fetchRatingsData();
    }
  }, [user]);

  const fetchRatingsData = async () => {
    try {
      if (user) {
        const list = await apiService.getUserRatings(user.id);
        setRatings(list);
        const stats = await apiService.getUserRatingStats(user.id);
        setRatingStats(stats);
      }
    } catch (err) {
      console.error("Failed to fetch ratings:", err);
    }
  };

  const handleUpdateProfile = async (e) => {
    e.preventDefault();
    setUpdating(true);
    setError('');
    setMessage('');
    try {
      await apiService.updateMe({
        full_name: fullName,
        gender: gender,
        phone: phone
      });
      setMessage('Profile details updated successfully.');
      await refreshProfile();
    } catch (err) {
      console.error(err);
      setError('Failed to update profile.');
    } finally {
      setUpdating(false);
    }
  };

  const handleAvatarChange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    setUploading(true);
    setError('');
    setMessage('');
    try {
      await apiService.uploadAvatar(file);
      setMessage('Avatar photo uploaded successfully.');
      await refreshProfile();
    } catch (err) {
      console.error(err);
      setError('Failed to upload image. Ensure it is a valid picture file.');
    } finally {
      setUploading(false);
    }
  };

  const handleRoleChange = async (role) => {
    setRoleSwitching(true);
    setError('');
    setMessage('');
    try {
      await apiService.saveRole({ role });
      setMessage(`Successfully switched to ${role === 'idea_owner' ? 'Idea Owner' : role === 'investor' ? 'Investor' : 'Freelancer'} role.`);
      
      // Refresh profile to trigger layout redirect
      const refreshed = await refreshProfile();
      updateProfileState(refreshed);
      
      // Redirect based on role
      window.location.reload();
    } catch (err) {
      console.error(err);
      setError('Failed to switch workspace mode.');
    } finally {
      setRoleSwitching(false);
    }
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">User Profile Settings</h1>
          <p className="page-subtitle">Configure your basic details, manage role views, and see reviews.</p>
        </div>
      </div>

      {message && (
        <div style={{ background: 'rgba(16, 185, 129, 0.1)', border: '1px solid rgba(16, 185, 129, 0.3)', padding: '12px', borderRadius: 'var(--border-radius-sm)', color: '#34d399', fontSize: '0.85rem', marginBottom: '24px' }}>
          {message}
        </div>
      )}

      {error && (
        <div style={{ background: 'rgba(239, 68, 68, 0.1)', border: '1px solid rgba(239, 68, 68, 0.3)', padding: '12px', borderRadius: 'var(--border-radius-sm)', color: '#f87171', fontSize: '0.85rem', marginBottom: '24px' }}>
          {error}
        </div>
      )}

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '30px', alignItems: 'start' }}>
        
        {/* Left Side: Avatar Card & Role Selection Switcher */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
          <div className="card glass-panel" style={{ padding: '30px', textAlign: 'center' }}>
            <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '16px', position: 'relative' }}>
              <div className="user-avatar" style={{ width: '100px', height: '100px', fontSize: '2.5rem' }}>
                {user?.profile_picture ? (
                  <img 
                    src={user.profile_picture.startsWith('http') ? user.profile_picture : `http://localhost:8000/${user.profile_picture}`} 
                    alt={user.full_name} 
                    style={{ width: '100px', height: '100px', borderRadius: '50%', objectFit: 'cover' }}
                  />
                ) : (
                  user?.full_name?.charAt(0).toUpperCase()
                )}
              </div>
              
              <label 
                htmlFor="avatar-upload" 
                style={{ 
                  position: 'absolute', 
                  bottom: 0, 
                  right: 'calc(50% - 50px)', 
                  background: 'var(--accent-purple)', 
                  borderRadius: '50%', 
                  width: '30px', 
                  height: '30px', 
                  display: 'flex', 
                  alignItems: 'center', 
                  justifyContent: 'center', 
                  cursor: 'pointer',
                  border: '2px solid var(--bg-primary)',
                  color: 'white'
                }}
                title="Upload Photo"
              >
                <Upload size={14} />
              </label>
              <input 
                id="avatar-upload"
                type="file"
                accept="image/*"
                onChange={handleAvatarChange}
                style={{ display: 'none' }}
                disabled={uploading}
              />
            </div>

            <h2 style={{ fontSize: '1.25rem', fontWeight: 600 }}>{user?.full_name}</h2>
            <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>{user?.email}</span>

            {/* Ratings Statistics Summary */}
            {ratingStats && ratingStats.count > 0 && (
              <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '4px', marginTop: '12px', color: 'var(--accent-yellow)' }}>
                <Star size={16} fill="var(--accent-yellow)" />
                <span style={{ fontWeight: 600, fontSize: '0.9rem', color: 'var(--text-primary)' }}>
                  {ratingStats.average.toFixed(1)} / 5.0
                </span>
                <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                  ({ratingStats.count} reviews)
                </span>
              </div>
            )}
          </div>

          {/* Role Switcher */}
          <div className="card glass-panel" style={{ padding: '24px' }}>
            <h3 style={{ fontSize: '1rem', fontWeight: 600, marginBottom: '16px', display: 'flex', alignItems: 'center', gap: '8px' }}>
              <RefreshCw size={16} /> Switch Workspace View
            </h3>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
              <button 
                className={`btn btn-sm ${user?.role === 'freelancer' ? 'btn-primary' : 'btn-secondary'}`}
                style={{ justifyContent: 'space-between', padding: '10px 16px' }}
                onClick={() => handleRoleChange('freelancer')}
                disabled={roleSwitching || user?.role === 'freelancer'}
              >
                <span>Freelancer Workspace</span>
                {user?.role === 'freelancer' && <Check size={14} />}
              </button>
              
              <button 
                className={`btn btn-sm ${user?.role === 'idea_owner' ? 'btn-primary' : 'btn-secondary'}`}
                style={{ justifyContent: 'space-between', padding: '10px 16px' }}
                onClick={() => handleRoleChange('idea_owner')}
                disabled={roleSwitching || user?.role === 'idea_owner'}
              >
                <span>Idea Owner Workspace</span>
                {user?.role === 'idea_owner' && <Check size={14} />}
              </button>

              <button 
                className={`btn btn-sm ${user?.role === 'investor' ? 'btn-primary' : 'btn-secondary'}`}
                style={{ justifyContent: 'space-between', padding: '10px 16px' }}
                onClick={() => handleRoleChange('investor')}
                disabled={roleSwitching || user?.role === 'investor'}
              >
                <span>Investor Workspace</span>
                {user?.role === 'investor' && <Check size={14} />}
              </button>
            </div>
          </div>
        </div>

        {/* Right Side: Edit Form, Profile Tags & Reviews list */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
          
          {/* Edit Form */}
          <div className="card glass-panel" style={{ padding: '30px' }}>
            <h3 style={{ fontSize: '1.15rem', fontWeight: 600, marginBottom: '20px' }}>Personal Information</h3>

            <form onSubmit={handleUpdateProfile}>
              <div className="form-group">
                <label className="form-label">Full Name</label>
                <input 
                  type="text" 
                  className="form-input" 
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                  required
                />
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}>
                <div className="form-group">
                  <label className="form-label">Gender</label>
                  <select 
                    className="form-input"
                    value={gender}
                    onChange={(e) => setGender(e.target.value)}
                    style={{ background: 'var(--bg-tertiary)', border: '1px solid var(--border-color)', height: '48px' }}
                  >
                    <option value="Male">Male</option>
                    <option value="Female">Female</option>
                    <option value="Other">Other</option>
                  </select>
                </div>

                <div className="form-group">
                  <label className="form-label">Phone Number</label>
                  <input 
                    type="text" 
                    className="form-input" 
                    value={phone}
                    onChange={(e) => setPhone(e.target.value)}
                    placeholder="+1 (234) 567-8900"
                  />
                </div>
              </div>

              <button type="submit" className="btn btn-primary" style={{ padding: '10px 24px', alignSelf: 'flex-start' }} disabled={updating}>
                Save Profile
              </button>
            </form>
          </div>

          {/* Tags (Skills, Interests, Goals) Display */}
          <div className="card glass-panel" style={{ padding: '30px' }}>
            <h3 style={{ fontSize: '1.15rem', fontWeight: 600, marginBottom: '20px', display: 'flex', alignItems: 'center', gap: '8px' }}>
              <Sparkles size={18} color="var(--accent-purple)" fill="var(--accent-purple-glow)" /> Core Profile Tags
            </h3>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
              <div>
                <h4 style={{ fontWeight: 600, fontSize: '0.9rem', color: 'var(--text-secondary)', marginBottom: '8px' }}>My Skills</h4>
                <div className="chip-container">
                  {user?.skills?.map((item) => (
                    <span key={item} className="chip">{item}</span>
                  ))}
                  {(!user?.skills || user.skills.length === 0) && <span style={{ color: 'var(--text-muted)', fontSize: '0.8rem' }}>None registered</span>}
                </div>
              </div>

              <div>
                <h4 style={{ fontWeight: 600, fontSize: '0.9rem', color: 'var(--text-secondary)', marginBottom: '8px' }}>Interests</h4>
                <div className="chip-container">
                  {user?.interests?.map((item) => (
                    <span key={item} className="chip" style={{ background: 'var(--accent-pink-glow)', borderColor: 'rgba(236, 72, 153, 0.2)' }}>{item}</span>
                  ))}
                  {(!user?.interests || user.interests.length === 0) && <span style={{ color: 'var(--text-muted)', fontSize: '0.8rem' }}>None registered</span>}
                </div>
              </div>

              <div>
                <h4 style={{ fontWeight: 600, fontSize: '0.9rem', color: 'var(--text-secondary)', marginBottom: '8px' }}>Career Goals</h4>
                <div className="chip-container">
                  {user?.goals?.map((item) => (
                    <span key={item} className="chip" style={{ background: 'var(--accent-green-glow)', borderColor: 'rgba(16, 185, 129, 0.2)' }}>{item}</span>
                  ))}
                  {(!user?.goals || user.goals.length === 0) && <span style={{ color: 'var(--text-muted)', fontSize: '0.8rem' }}>None registered</span>}
                </div>
              </div>
            </div>
          </div>

          {/* User Reviews */}
          {ratings.length > 0 && (
            <div className="card glass-panel" style={{ padding: '30px' }}>
              <h3 style={{ fontSize: '1.15rem', fontWeight: 600, marginBottom: '20px' }}>User Reviews</h3>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                {ratings.map((review) => (
                  <div key={review.id} style={{ borderBottom: '1px solid var(--border-color)', paddingBottom: '12px' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '6px' }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '2px', color: 'var(--accent-yellow)' }}>
                        {[...Array(5)].map((_, i) => (
                          <Star 
                            key={i} 
                            size={12} 
                            fill={i < review.rating ? 'var(--accent-yellow)' : 'none'} 
                            color="var(--accent-yellow)" 
                          />
                        ))}
                      </div>
                      <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                        {new Date(review.created_at).toLocaleDateString()}
                      </span>
                    </div>
                    {review.review_text && (
                      <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', fontStyle: 'italic' }}>
                        "{review.review_text}"
                      </p>
                    )}
                  </div>
                ))}
              </div>
            </div>
          )}

        </div>

      </div>
    </div>
  );
};

export default Profile;
