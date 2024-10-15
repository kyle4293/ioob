import apiClient from './axiosConfig';

export const boardService = {
  createBoard: async (boardData) => {
    const response = await apiClient.post(`/api/boards`, boardData);
    return response.data;
  },

  getBoards: async (projectId) => {
    const response = await apiClient.get(`/api/boards?projectId=${projectId}`);
    return response.data;
  },

  editBoard: async (boardId, updatedBoard) => {
    const response = await apiClient.put(`/api/boards/${boardId}`, updatedBoard);
    return response.data;
  },

  deleteBoard: async (boardId) => {
    await apiClient.delete(`/api/boards/${boardId}`);
  },
};
