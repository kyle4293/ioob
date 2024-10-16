import React, { useEffect, useState } from 'react';
import { projectService } from '../services/ProjectService'; // 프로젝트 서비스
import { taskService } from '../services/TaskService'; // 테스크 서비스
import { useNavigate } from 'react-router-dom'; // 페이지 이동을 위한 useNavigate

const Home = () => {
  const [projects, setProjects] = useState([]);
  const [tasks, setTasks] = useState([]);
  const [error, setError] = useState(null);
  const navigate = useNavigate(); // 페이지 이동을 위한 네비게이트 훅

  useEffect(() => {
    // 프로젝트 목록 가져오기
    const fetchProjects = async () => {
      try {
        const projectData = await projectService.getMyProjects();
        setProjects(projectData);
      } catch (err) {
        if (err.response && err.response.status === 404) {
          setError('프로젝트가 없습니다.');
        } else {
          setError('프로젝트 목록을 불러오는 중 오류가 발생했습니다.');
        }
      }
    };

    // 할당된 작업 목록 가져오기
    const fetchTasks = async () => {
      try {
        const taskData = await taskService.getMyTasks();
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

  // 프로젝트 클릭 시 상세 페이지로 이동
  const handleProjectClick = (projectId) => {
    navigate(`/projects/${projectId}`);
  };

  // 작업 클릭 시 상세 페이지로 이동
  const handleTaskClick = (taskId) => {
    navigate(`/tasks/${taskId}`);
  };

  return (
    <div className="home-container">
      <h1>내 작업</h1>

      {/* 오류 발생 시 메시지 표시 */}
      {error && <p>{error}</p>}

      <div className="sections-container">
        {/* 프로젝트 목록 */}
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

        {/* 할당된 작업 목록 */}
        <section>
          <h2>작업</h2>
          {tasks.length > 0 ? (
            <ul>
              {tasks.map((task) => (
                <li key={task.id} onClick={() => handleTaskClick(task.id)}>
                  <strong>{task.title}</strong> - 상태: {task.status}
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
