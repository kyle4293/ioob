import apiClient from './axiosConfig';

export const boardService = {
  createBoard: async (projectId, boardData) => {
    const response = await apiClient.post(`/api/projects/${projectId}/boards`, boardData);
    return response.data;
  },

  getBoards: async (projectId) => {
    const response = await apiClient.get(`/api/projects/${projectId}/boards`);
    return response.data;
  },

  editBoard: async (projectId, boardId, updatedBoard) => {
    const response = await apiClient.put(`/api/projects/${projectId}/boards/${boardId}`, updatedBoard);
    return response.data;
  },

  deleteBoard: async (projectId, boardId) => {
    await apiClient.delete(`/api/projects/${projectId}/boards/${boardId}`);
  },
};
