import apiClient from './axiosConfig';

export const adminService = {
  getUsers: async () => {
    const response = await apiClient.get('/api/backoffice/users');
    return response.data;
  },

  deleteUser: async (id) => {
    await apiClient.delete(`/api/backoffice/users/${id}`);
  },
};
