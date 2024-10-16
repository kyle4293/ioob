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

  getComments: async (taskId) => {
    const response = await apiClient.get(`/api/tasks/${taskId}/comments`);
    return response.data;
  },

  addComment: async (taskId, commentData) => {
    const response = await apiClient.post(`/api/tasks/${taskId}/comments`, commentData);
    return response.data;
  },

  updateComment: async (taskId, commentId, commentData) => {
    const response = await apiClient.put(`/api/tasks/${taskId}/comments/${commentId}`, commentData);
    return response.data;
  },

  deleteComment: async (taskId, commentId) => {
    await apiClient.delete(`/api/tasks/${taskId}/comments/${commentId}`);
  },

  getMyTasks: async () => {
    const response = await apiClient.get('/api/tasks/my-tasks');
    return response.data;
  },
};
