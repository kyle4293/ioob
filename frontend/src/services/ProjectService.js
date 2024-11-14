import apiClient from './axiosConfig';

export const projectService = {
  createProject: async (projectData) => {
    const response = await apiClient.post(`/api/projects`, projectData);
    return response.data;
  },

  getAllProjects: async () => {
    const response = await apiClient.get('/api/projects');
    return response.data;
  },

  getProjectDetails: async (projectId) => {
    const response = await apiClient.get(`/api/projects/${projectId}`);
    return response.data;
  },

  updateProject: async (projectId, projectData) => {
    const response = await apiClient.put(`/api/projects/${projectId}`, projectData);
    return response.data;
  },

  deleteProject: async (id) => {
    await apiClient.delete(`/api/projects/${id}`);
  },

  addUserToProject: async (projectId, userEmail, role) => {
    const response = await apiClient.post(`/api/projects/${projectId}/assign-role`, {
      userEmail,
      role
    });
    return response.data;
  },

  getUsersInProject: async (projectId) => {
    const response = await apiClient.get(`/api/projects/${projectId}/users`);
    return response.data;
  },
};
