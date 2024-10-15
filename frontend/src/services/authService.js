import apiClient from './axiosConfig';

export const authService = {
  login: async (credentials) => {
    const response = await apiClient.post('/api/auth/login', credentials);
    if (response.status === 200) {
      localStorage.setItem('userEmail', response.data.email);
  }
    return response.data;
  },

  register: async (data) => {
    const response = await apiClient.post('/api/auth/register', data);
    return response.data;
  },

  logout: async () => {
    await apiClient.post('/api/auth/logout');
  },

  getProfile: async () => {
    const response = await apiClient.get('/api/users/profile');
    return response.data;
  }
};
