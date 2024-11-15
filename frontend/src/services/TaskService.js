import apiClient from './axiosConfig';

export const taskService = {
  createTask: async (projectId, boardId, taskData) => {
    const response = await apiClient.post(`/api/projects/${projectId}/boards/${boardId}/tasks`, taskData);
    return response.data;
  },

  getTasksByBoardId: async (projectId, boardId) => {
    const response = await apiClient.get(`/api/projects/${projectId}/boards/${boardId}/tasks`);
    return response.data;
  },

  getTaskDetails: async (projectId, boardId, taskId) => {
    const response = await apiClient.get(`/api/projects/${projectId}/boards/${boardId}/tasks/${taskId}`);
    return response.data;
  },

  updateTask: async (projectId, boardId, taskId, updatedTask) => {
    const response = await apiClient.put(`/api/projects/${projectId}/boards/${boardId}/tasks/${taskId}`, updatedTask);
    return response.data;
  },

  moveTaskToBoard: async (projectId, boardId, taskId) => {
    const response = await apiClient.put(`/api/projects/${projectId}/boards/${boardId}/tasks/${taskId}/move`);
    return response.data;
  },

  deleteTask: async (projectId, boardId, taskId) => {
    await apiClient.delete(`/api/projects/${projectId}/boards/${boardId}/tasks/${taskId}`);
  },

  getComments: async (projectId, boardId, taskId) => {
    const response = await apiClient.get(`/api/projects/${projectId}/boards/${boardId}/tasks/${taskId}/comments`);
    return response.data;
  },

  addComment: async (projectId, boardId, taskId, commentData) => {
    const response = await apiClient.post(`/api/projects/${projectId}/boards/${boardId}/tasks/${taskId}/comments`, commentData);
    return response.data;
  },

  updateComment: async (projectId, boardId, taskId, commentId, commentData) => {
    const response = await apiClient.put(`/api/projects/${projectId}/boards/${boardId}/tasks/${taskId}/comments/${commentId}`, commentData);
    return response.data;
  },

  deleteComment: async (projectId, boardId, taskId, commentId) => {
    await apiClient.delete(`/api/projects/${projectId}/boards/${boardId}/tasks/${taskId}/comments/${commentId}`);
  },
};
