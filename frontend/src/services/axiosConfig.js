import axios from 'axios';
import { authService } from './authService';

const apiClient = axios.create({
  baseURL: 'http://localhost:8080/', 
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, 
});

// Access Token이 만료된 경우 refreshToken으로 재발급 처리
apiClient.interceptors.response.use(
  response => response,
  async (error) => {
    const originalRequest = error.config;

    // 401 에러가 발생하고, originalRequest가 이미 한번 시도되지 않은 경우에만 처리
    if (error.response.status === 401 && !originalRequest._retry) {
      console.log("refresh")
      originalRequest._retry = true;

      try {
        const hasRefreshToken = localStorage.getItem('userEmail');
        if (!hasRefreshToken) {
          authService.logout();
          window.location.href = '/login'; 
          return Promise.reject(error);
        }

        // Access Token 재발급
        await axios.post('http://localhost:8080/api/auth/refresh', {}, { withCredentials: true });

        // 재발급 받은 후 원래의 요청 다시 시도
        return apiClient(originalRequest);
      } catch (err) {
        console.error('Token 재발급 실패', err);
        authService.logout(); 
        localStorage.removeItem('userEmail'); 
        window.location.href = '/login'; 
        return Promise.reject(error);
      }
    }

    return Promise.reject(error);
  }
);

export default apiClient;
