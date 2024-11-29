import React, { useEffect, useState } from 'react';
import { projectService } from '../../services/ProjectService';

const ProjectUserList = ({ projectId, onClose }) => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [activeRoleEdit, setActiveRoleEdit] = useState(null); // 현재 활성화된 권한 편집 사용자 이메일

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const response = await projectService.getUsersInProject(projectId);
        setUsers(response);
      } catch (error) {
        console.error('유저 목록을 불러오는 중 오류가 발생했습니다:', error);
      }
    };

    fetchUsers();
  }, [projectId]);

  const handleRoleChange = async (userEmail, newRole) => {
    setLoading(true);
    try {
      await projectService.addUserToProject(projectId, userEmail, newRole);
      setUsers((prevUsers) =>
        prevUsers.map((user) =>
          user.email === userEmail ? { ...user, role: newRole } : user
        )
      );
      alert('권한이 성공적으로 변경되었습니다.');
    } catch (error) {
      alert('권한이 없습니다.');
    } finally {
      setLoading(false);
      setActiveRoleEdit(null); 
    }
  };

  return (
    <div className="modal">
      <div className="modal-content">
        <h3>프로젝트 사용자 목록</h3>
        <ul>
          {users.map((user) => (
            <li className="user" key={user.email}>
              <div className="user-info">
                <p>{user.name} ({user.email})</p>
                <p
                  className="user-role"
                  onClick={() =>
                    setActiveRoleEdit(activeRoleEdit === user.email ? null : user.email)
                  }
                >
                  {user.role === 'ROLE_PROJECT_ADMIN' ? 'Admin' : 'User'}
                </p>
              </div>
              {activeRoleEdit === user.email && (
                <div className="role-actions">
                  {user.role !== 'ROLE_PROJECT_ADMIN' && (
                    <button
                      onClick={() => handleRoleChange(user.email, 'ROLE_PROJECT_ADMIN')}
                      disabled={loading}
                    >
                      Admin으로 변경
                    </button>
                  )}
                  {user.role !== 'ROLE_USER' && (
                    <button
                      onClick={() => handleRoleChange(user.email, 'ROLE_USER')}
                      disabled={loading}
                    >
                      User로 변경
                    </button>
                  )}
                </div>
              )}
            </li>
          ))}
        </ul>
        <div className="modal-actions">
          <button onClick={onClose}>닫기</button>
        </div>
      </div>
    </div>
  );
};

export default ProjectUserList;
