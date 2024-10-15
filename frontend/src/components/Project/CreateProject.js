import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { projectService } from '../../services/ProjectService';

const CreateProject = () => {
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await projectService.createProject({ name, description });
      navigate('/projects');  // 프로젝트 목록 페이지로 이동
    } catch (error) {
      alert('프로젝트 생성 실패');
    }
  };

  return (
    <div className="create-project">
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
        <button type="submit">생성</button>
      </form>
    </div>
  );
};

export default CreateProject;
