import apiClient from './axiosConfig';

export const authService = {
  login: async (credentials) => {
    try {
      const response = await apiClient.post('/api/auth/login', credentials);
      if (response.status === 200) {
        localStorage.setItem('userEmail', response.data.email);
        return response.data;
      }
    } catch (error) {
      if (error.response && error.response.data && error.response.data.error) {
        throw new Error(error.response.data.error); // 서버에서 받은 오류 메시지를 throw
      }
      throw new Error('알 수 없는 오류가 발생했습니다.'); // 기타 오류
    }
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
