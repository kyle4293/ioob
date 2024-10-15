import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { projectService } from '../../services/ProjectService';

const ProjectList = () => {
  const [projects, setProjects] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchProjects = async () => {
      try {
        const data = await projectService.getAllProjects();
        setProjects(data);
      } catch (error) {
        console.error('프로젝트 목록을 가져오는 중 오류 발생:', error);
      }
    };
    fetchProjects();
  }, []);

  const handleProjectClick = (id) => {
    navigate(`/projects/${id}`);
  };

  const handleCreateProject = () => {
    navigate('/projects/new');  // 프로젝트 생성 페이지로 이동
  };

  return (
    <div className="project-list">
      <h2>프로젝트 목록</h2>
      <button onClick={handleCreateProject}>새 프로젝트 생성</button>
      <ul>
        {projects.map((project) => (
          <li key={project.id} onClick={() => handleProjectClick(project.id)}>
            {project.name}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ProjectList;
