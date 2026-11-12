import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8000';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Automatically inject JWT token if it exists in localStorage
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Optional: Intercept responses to handle auth errors (e.g. expired token)
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Clear token and redirect to login if unauthorized
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      if (window.location.pathname !== '/login' && window.location.pathname !== '/signup') {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export const apiService = {
  // ── Auth ──
  register: async (data) => {
    const response = await api.post('/auth/register', data);
    return response.data; // Expected { access_token, token_type }
  },
  login: async (data) => {
    const response = await api.post('/auth/login', data);
    return response.data;
  },

  // ── Onboarding ──
  saveRole: async (data) => {
    const response = await api.post('/onboarding/role', data);
    return response.data;
  },
  saveSkills: async (data) => {
    const response = await api.post('/onboarding/skills', data);
    return response.data;
  },
  saveInterests: async (data) => {
    const response = await api.post('/onboarding/interests', data);
    return response.data;
  },
  saveGoals: async (data) => {
    const response = await api.post('/onboarding/goals', data);
    return response.data;
  },

  // ── Users ──
  getMe: async () => {
    const response = await api.get('/users/me');
    return response.data;
  },
  updateMe: async (data) => {
    const response = await api.put('/users/me', data);
    return response.data;
  },
  uploadAvatar: async (file) => {
    const formData = new FormData();
    formData.append('file', file);
    const response = await api.post('/users/avatar', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  },

  // ── Projects ──
  listProjects: async (params = {}) => {
    const response = await api.get('/projects/', { params });
    return response.data;
  },
  myProjects: async () => {
    const response = await api.get('/projects/mine');
    return response.data;
  },
  getProject: async (id) => {
    const response = await api.get(`/projects/${id}`);
    return response.data;
  },
  createProject: async (data) => {
    const response = await api.post('/projects/', data);
    return response.data;
  },
  updateProject: async (id, data) => {
    const response = await api.put(`/projects/${id}`, data);
    return response.data;
  },
  deleteProject: async (id) => {
    const response = await api.delete(`/projects/${id}`);
    return response.data;
  },
  applyToProject: async (id, data) => {
    const response = await api.post(`/projects/${id}/apply`, data);
    return response.data;
  },
  listApplicants: async (id) => {
    const response = await api.get(`/projects/${id}/applicants`);
    return response.data;
  },
  updateApplicant: async (id, uid, data) => {
    const response = await api.put(`/projects/${id}/applicants/${uid}`, data);
    return response.data;
  },
  listProjectMembers: async (id) => {
    const response = await api.get(`/projects/${id}/members`);
    return response.data;
  },
  removeProjectMember: async (id, uid) => {
    const response = await api.delete(`/projects/${id}/members/${uid}`);
    return response.data;
  },
  generateIdea: async (title, category) => {
    const response = await api.get('/projects/generate-idea', {
      params: { title, category },
    });
    return response.data;
  },

  // ── Chat ──
  listConversations: async () => {
    const response = await api.get('/chat/conversations');
    return response.data;
  },
  createConversation: async (data) => {
    const response = await api.post('/chat/conversations', data);
    return response.data;
  },
  getProjectConversation: async (id) => {
    const response = await api.get(`/chat/project/${id}`);
    return response.data;
  },
  getMessages: async (id, sinceId = null) => {
    const params = sinceId ? { since_id: sinceId } : {};
    const response = await api.get(`/chat/conversations/${id}/messages`, { params });
    return response.data;
  },
  sendMessage: async (id, data) => {
    const response = await api.post(`/chat/conversations/${id}/messages`, data);
    return response.data;
  },
  getChatHighlights: async (id) => {
    const response = await api.get(`/chat/conversations/${id}/highlights`);
    return response.data;
  },

  // ── Funding ──
  fundingProjects: async () => {
    const response = await api.get('/funding/projects');
    return response.data;
  },
  invest: async (id, data) => {
    const response = await api.post(`/funding/${id}/invest`, data);
    return response.data;
  },
  myPortfolio: async () => {
    const response = await api.get('/funding/portfolio');
    return response.data;
  },
  fundingStats: async () => {
    const response = await api.get('/funding/stats');
    return response.data;
  },

  // ── AI ──
  getRecommendations: async () => {
    const response = await api.get('/ai/recommendations');
    return response.data;
  },
  getSkillAnalysis: async () => {
    const response = await api.get('/ai/skill-analysis');
    return response.data;
  },
  getMatchScore: async (id) => {
    const response = await api.get(`/ai/match-score/${id}`);
    return response.data;
  },
  getProjectAnalysis: async (id) => {
    const response = await api.get(`/ai/project-analysis/${id}`);
    return response.data;
  },

  // ── Notifications ──
  listNotifications: async () => {
    const response = await api.get('/notifications/');
    return response.data;
  },
  unreadCount: async () => {
    const response = await api.get('/notifications/unread-count');
    return response.data;
  },
  markRead: async (id) => {
    const response = await api.put(`/notifications/${id}/read`);
    return response.data;
  },
  markAllRead: async () => {
    const response = await api.put('/notifications/read-all');
    return response.data;
  },

  // ── Ratings ──
  submitRating: async (data) => {
    const response = await api.post('/ratings/', data);
    return response.data;
  },
  getUserRatings: async (id) => {
    const response = await api.get(`/ratings/user/${id}`);
    return response.data;
  },
  getUserRatingStats: async (id) => {
    const response = await api.get(`/ratings/stats/${id}`);
    return response.data;
  },
};
export default apiService;
