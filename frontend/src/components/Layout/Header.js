import React, { useContext, useState, useRef, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../../context/AuthContext';
import ProjectModal from '../Project/ProjectModal';

const Header = () => {
  const { user, logout } = useContext(AuthContext);
  const navigate = useNavigate();
  const [isProjectModalOpen, setIsProjectModalOpen] = useState(false);

  const [isProjectDropdownOpen, setProjectDropdownOpen] = useState(false);
  const [isProfileDropdownOpen, setProfileDropdownOpen] = useState(false);

  const projectDropdownRef = useRef(null);
  const profileDropdownRef = useRef(null);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const handleClickOutside = (event) => {
    if (
      projectDropdownRef.current &&
      !projectDropdownRef.current.contains(event.target)
    ) {
      setProjectDropdownOpen(false);
    }
    if (
      profileDropdownRef.current &&
      !profileDropdownRef.current.contains(event.target)
    ) {
      setProfileDropdownOpen(false);
    }
  };

  useEffect(() => {
    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  return (
    <header className="header">
      <div className="header-left">
        <div className="logo">
          <Link to="/">IOOB</Link>
        </div>
        {user && (
          <div className="header-project">
            <button
              className="header-project-button"
              onClick={() => setProjectDropdownOpen(!isProjectDropdownOpen)}
            >
              프로젝트
            </button>
            {isProjectDropdownOpen && (
              <div
                className="header-project-dropdown-menu"
                ref={projectDropdownRef}
              >
                <Link 
                  to="/projects" 
                  onClick={() => setProjectDropdownOpen(false)}
                >
                  모든 프로젝트 보기
                </Link>
                <button
                  onClick={() => {
                    setIsProjectModalOpen(true);
                    setProjectDropdownOpen(false);
                  }}
                >
                  프로젝트 만들기
                </button>
              </div>
            )}
          </div>
        )}
      </div>

      {user ? (
        <div className="header-right">
          <button
            className="header-profile-button"
            onClick={() => setProfileDropdownOpen(!isProfileDropdownOpen)}
          >
            프로필
          </button>
          {isProfileDropdownOpen && (
            <div
              className="header-profile-dropdown-menu"
              ref={profileDropdownRef}
            >
              <div className="profile-info">
                <small>{user.email}</small>
              </div>
              <Link 
                to="/profile" 
                onClick={() => setProfileDropdownOpen(false)}
              >
                계정 관리
              </Link>
              <button
                onClick={() => {
                  handleLogout();
                  setProfileDropdownOpen(false);
                }}
              >
                로그아웃
              </button>
            </div>
          )}
        </div>
      ) : (
        <div className="header-right">
          <Link to="/login" className="auth-link">로그인</Link>
          <Link to="/register" className="auth-link">회원가입</Link>
        </div>
      )}

    {isProjectModalOpen && <ProjectModal onClose={() => setIsProjectModalOpen(false)} />}
    </header>
  );
};

export default Header;
