import apiClient from './axiosConfig';

export const taskService = {
  createTask: async (taskData) => {
    const response = await apiClient.post(`/api/tasks`, taskData);
    return response.data;
  },

  getTasks: async (boardId) => {
    const response = await apiClient.get(`/api/tasks?boardId=${boardId}`);
    return response.data;
  },

  getTaskDetails: async (taskId) => {
    const response = await apiClient.get(`/api/tasks/${taskId}`);
    return response.data;
  },

  deleteTask: async (taskId) => {
    await apiClient.delete(`/api/tasks/${taskId}`);
  },
};
