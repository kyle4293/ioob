import axios from 'axios';
import { authService } from './authService'; // authService 사용하여 로그아웃 처리

// Axios 인스턴스 생성
const apiClient = axios.create({
  baseURL: 'http://localhost:8080/',  // 올바른 baseURL 설정 (8080)
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,  // 쿠키를 요청에 포함
});

// Access Token이 만료된 경우 refreshToken으로 재발급 처리
apiClient.interceptors.response.use(
  response => response,
  async (error) => {
    const originalRequest = error.config;

    // 401 에러가 발생하고, originalRequest가 이미 한번 시도되지 않은 경우에만 처리
    if (error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        // Refresh Token이 없으면 무한 요청을 방지하고, 로그인 페이지로 리다이렉트
        const hasRefreshToken = localStorage.getItem('userEmail');
        if (!hasRefreshToken) {
          authService.logout(); // Refresh Token이 없으면 로그아웃 처리
          window.location.href = '/login'; // 로그인 페이지로 이동
          return Promise.reject(error);
        }

        // Access Token 재발급 (baseURL이 포함된 경로로 수정)
        await axios.post('http://localhost:8080/api/auth/refresh', {}, { withCredentials: true });

        // 재발급 받은 후 원래의 요청 다시 시도
        return apiClient(originalRequest);
      } catch (err) {
        console.error('Token 재발급 실패', err);
        authService.logout(); // Token 재발급 실패 시 로그아웃 처리
        window.location.href = '/login'; // 로그인 페이지로 이동
        return Promise.reject(error);
      }
    }

    return Promise.reject(error);
  }
);

export default apiClient;
