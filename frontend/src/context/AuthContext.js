import React, { createContext, useEffect, useState } from 'react';
import { authService } from '../services/authService';

// AuthContext 생성
export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    // 컴포넌트가 처음 마운트될 때 로컬 스토리지에서 이메일 가져오기
    const storedEmail = localStorage.getItem('userEmail');
    return storedEmail ? { email: storedEmail } : null;
  });

  useEffect(() => {
    const checkUser = async () => {
      try {
        // 로컬 스토리지에 사용자 이메일이 있을 때만 프로필 요청
        const storedEmail = localStorage.getItem('userEmail');
        if (storedEmail && !user) {
          const profile = await authService.getProfile();
          setUser(profile); // 프로필 데이터를 가져와 상태에 저장
        }
      } catch (err) {
        setUser(null);
        localStorage.removeItem('userEmail'); // 오류 시 로컬 스토리지에서 이메일 삭제
      }
    };

    checkUser();
  }, [user]);

  const login = async (credentials) => {
    const response = await authService.login(credentials);
    setUser(response); // 로그인 후 응답 데이터(프로필)를 상태에 저장
  };

  const logout = async () => {
    await authService.logout();
    setUser(null);
    localStorage.removeItem('userEmail'); // 로그아웃 시 로컬 스토리지에서 이메일 삭제
  };

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};
