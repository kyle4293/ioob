import React, { useEffect, useState } from 'react';
import { projectService } from '../../services/ProjectService';

const ProjectUserList = ({ projectId, onClose }) => {
  const [users, setUsers] = useState([]);

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

  return (
    <div className="modal">
      <div className="modal-content">
        <h3>프로젝트 사용자 목록</h3>
        <ul>
          {users.map(user => (
            <li className='user' key={user.userEmail}>
              <p>{user.userName} ({user.userEmail})</p> 
              <p>{user.role === 'ROLE_PROJECT_ADMIN' ? 'Admin' : 'User'}</p>
            </li>
          ))}
        </ul>
        <button onClick={onClose}>닫기</button>
      </div>
    </div>
  );
};

export default ProjectUserList;
