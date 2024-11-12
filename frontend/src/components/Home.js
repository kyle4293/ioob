import React, { useEffect, useState } from 'react';
import { authService } from '../services/authService';
import { useNavigate } from 'react-router-dom'; 

const Home = () => {
  const [projects, setProjects] = useState([]);
  const [tasks, setTasks] = useState([]);
  const [error, setError] = useState(null);
  const navigate = useNavigate(); 

  useEffect(() => {
    const fetchProjects = async () => {
      try {
        const projectData = await authService.getUserProjects();
        setProjects(projectData);
      } catch (err) {
        if (err.response && err.response.status === 404) {
          setError('프로젝트가 없습니다.');
        } else {
          setError('프로젝트 목록을 불러오는 중 오류가 발생했습니다.');
        }
      }
    };

    const fetchTasks = async () => {
      try {
        const taskData = await authService.getUserTasks();
        setTasks(taskData);
      } catch (err) {
        if (err.response && err.response.status === 404) {
          setError('할당된 작업이 없습니다.');
        } else {
          setError('할당된 작업을 불러오는 중 오류가 발생했습니다.');
        }
      }
    };

    fetchProjects();
    fetchTasks();
  }, []);

  const handleProjectClick = (projectId) => {
    navigate(`/projects/${projectId}`);
  };

  const handleTaskClick = (projectId, boardId, taskId) => {
    navigate(`/projects/${projectId}/boards/${boardId}/tasks/${taskId}`);
  };  

  return (
    <div className="home-container">
      <h1>내 작업</h1>

      {error && <p>{error}</p>}

      <div className="sections-container">
        <section>
          <h2>프로젝트</h2>
          {projects.length > 0 ? (
            <ul>
              {projects.map((project) => (
                <li key={project.id} onClick={() => handleProjectClick(project.id)}>
                  <strong>{project.name}</strong> - {project.description}
                </li>
              ))}
            </ul>
          ) : (
            <p>프로젝트가 없습니다.</p>
          )}
        </section>

        <section>
          <h2>작업</h2>
          {tasks.length > 0 ? (
            <ul>
              {tasks.map((task) => (
                <li key={task.id} onClick={() => handleTaskClick(task.projectId, task.boardId, task.id)}>
                  <strong>{task.title}</strong> - 프로젝트: {task.projectName}
                </li>
              ))}
            </ul>
          ) : (
            <p>할당된 작업이 없습니다.</p>
          )}
        </section>
      </div>
    </div>
  );
};

export default Home;
