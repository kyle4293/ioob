import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { projectService } from '../../services/ProjectService';

const ProjectModal = ({ onClose }) => {
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await projectService.createProject({ name, description });
      navigate(0); 
    } catch (error) {
      alert('프로젝트 생성 실패');
    }
  };

  return (
    <div className="modal">
      <div className="modal-content">
        <h2>새 프로젝트 생성</h2>
        <form onSubmit={handleSubmit}>
          <input
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="프로젝트 이름"
            required
          />
          <textarea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="프로젝트 설명"
          />
          <div className="modal-buttons">
            <button type="submit">생성</button>
            <button type="button" onClick={onClose}>취소</button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ProjectModal;
