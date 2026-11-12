import React, { createContext, useContext, useState, useEffect } from 'react';
import apiService from '../services/api';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('token') || null);
  const [loading, setLoading] = useState(true);

  // Initialize auth state by fetching user profile if token is present
  useEffect(() => {
    const initializeAuth = async () => {
      if (token) {
        try {
          const profile = await apiService.getMe();
          setUser(profile);
          localStorage.setItem('user', JSON.stringify(profile));
        } catch (error) {
          console.error("Auth initialization failed:", error);
          // Token is likely invalid or expired
          logout();
        }
      }
      setLoading(false);
    };

    initializeAuth();
  }, [token]);

  const login = async (email, password) => {
    setLoading(true);
    try {
      const authData = await apiService.login({ email, password });
      const accessToken = authData.access_token;
      
      localStorage.setItem('token', accessToken);
      setToken(accessToken);
      
      // Fetch user profile immediately
      const profile = await apiService.getMe();
      setUser(profile);
      localStorage.setItem('user', JSON.stringify(profile));
      setLoading(false);
      return profile;
    } catch (error) {
      setLoading(false);
      throw error;
    }
  };

  const register = async (email, password, fullName, phone) => {
    setLoading(true);
    try {
      const authData = await apiService.register({ email, password, confirm_password: password, full_name: fullName, phone });
      const accessToken = authData.access_token;
      
      localStorage.setItem('token', accessToken);
      setToken(accessToken);
      
      // Fetch user profile
      const profile = await apiService.getMe();
      setUser(profile);
      localStorage.setItem('user', JSON.stringify(profile));
      setLoading(false);
      return profile;
    } catch (error) {
      setLoading(false);
      throw error;
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setToken(null);
    setUser(null);
  };

  const updateProfileState = (updatedProfile) => {
    setUser(updatedProfile);
    localStorage.setItem('user', JSON.stringify(updatedProfile));
  };

  const refreshProfile = async () => {
    try {
      const profile = await apiService.getMe();
      setUser(profile);
      localStorage.setItem('user', JSON.stringify(profile));
      return profile;
    } catch (error) {
      console.error("Failed to refresh user profile:", error);
    }
  };

  return (
    <AuthContext.Provider value={{ user, token, loading, login, register, logout, updateProfileState, refreshProfile }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
