import React, { useState } from 'react';
import { projectService } from '../../services/ProjectService';

const AddUserModal = ({ projectId, onClose }) => {
  const [userEmail, setUserEmail] = useState('');
  const [role, setRole] = useState('ROLE_USER'); // 기본값은 일반 사용자

  const handleAddUser = async () => {
    try {
      await projectService.addUserToProject(projectId, userEmail, role);
      alert('사용자가 성공적으로 추가되었습니다.');
      onClose();
    } catch (error) {
      console.error('사용자 추가 중 오류 발생:', error);
      alert('사용자 추가에 실패했습니다.');
    }
  };

  return (
    <div className="modal">
      <div className="modal-content">
        <h3>사용자 추가</h3>
        <input
          type="email"
          placeholder="사용자 이메일"
          value={userEmail}
          onChange={(e) => setUserEmail(e.target.value)}
        />
        <select value={role} onChange={(e) => setRole(e.target.value)}>
          <option value="ROLE_USER">일반 사용자</option>
          <option value="ROLE_PROJECT_ADMIN">프로젝트 관리자</option>
        </select>
        <div className="modal-actions">
          <button onClick={handleAddUser}>추가</button>
          <button onClick={onClose}>닫기</button>
        </div>
      </div>
    </div>
  );
};

export default AddUserModal;
