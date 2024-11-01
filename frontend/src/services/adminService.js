import apiClient from './axiosConfig';

export const adminService = {
  getUsers: async () => {
    const response = await apiClient.get('/api/admin/users');
    return response.data;
  },

  getUser: async (id) => {
    const response = await apiClient.get(`/api/admin/users/${id}`);
    return response.data;
  },

  deleteUser: async (id) => {
    await apiClient.delete(`/api/admin/users/${id}`);
  },
};
