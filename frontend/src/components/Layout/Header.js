import React, { useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../../context/AuthContext';

const Header = () => {
  const { user, logout } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="header">
      <div className="logo">
        <Link to="/">IOOB</Link>
      </div>
      <nav>
        <ul>
          <li><Link to="/projects">프로젝트</Link></li>
          {user && user.email ? (
            <>
              <li><Link to="/profile">프로필</Link></li>
              <li><button className="logout-button" onClick={handleLogout}>로그아웃</button></li>
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
