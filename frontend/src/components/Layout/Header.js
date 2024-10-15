import React, { useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../../context/AuthContext';

const Header = () => {
  const { user, logout } = useContext(AuthContext); // AuthContext에서 사용자 정보와 로그아웃 함수 가져오기
  const navigate = useNavigate();

  const handleLogout = () => {
    logout(); // 로그아웃 처리
    navigate('/login'); // 로그아웃 후 로그인 페이지로 이동
  };

  return (
    <header className="header">
      <nav>
        <ul>
          <li><Link to="/">홈</Link></li>
          <li><Link to="/projects">프로젝트</Link></li>
          {user && user.email ? ( // user가 존재하고 이메일이 있는지 확인
            <>
              <li><Link to="/profile">프로필</Link></li>
              <li><button onClick={handleLogout}>로그아웃</button></li>
            </>
          ) : (
            <>
              <li><Link to="/login">로그인</Link></li>
              <li><Link to="/register">회원가입</Link></li>
            </>
          )}
        </ul>
      </nav>
    </header>
  );
};

export default Header;
