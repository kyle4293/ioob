import React, { useEffect, useState } from 'react';
import { authService } from '../services/authService';
import { useNavigate } from 'react-router-dom'; 
import dayjs from 'dayjs';

const Home = () => {
  const [projects, setProjects] = useState([]);
  const [tasks, setTasks] = useState([]);
  const [error, setError] = useState(null);
  const navigate = useNavigate(); 

  useEffect(() => {
    const fetchProjects = async () => {
      try {
        const projectData = await authService.getUserProjects();
        const sortedProjects = projectData.sort((a, b) => new Date(b.modifiedAt) - new Date(a.modifiedAt));
        setProjects(sortedProjects);
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
        const sortedTasks = taskData.sort((a, b) => new Date(b.modifiedAt) - new Date(a.modifiedAt));
        setTasks(sortedTasks);
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

  // 날짜별로 작업을 그룹화
  const groupTasksByDate = (tasks) => {
    return tasks.reduce((acc, task) => {
      const date = dayjs(task.modifiedAt).format('YYYY-MM-DD');
      if (!acc[date]) acc[date] = [];
      acc[date].push(task);
      return acc;
    }, {});
  };

  const groupedTasks = groupTasksByDate(tasks);

  return (
    <div className="home-container">
      <h2>내 작업</h2>
      <hr />

      {error && <p>{error}</p>}

      <div className="sections-container">
        <section>
          <h3>최근 프로젝트</h3>
          <div className="project-cards">
            {projects.length > 0 ? (
              projects.map((project) => (
                <div key={project.id} className="card" onClick={() => handleProjectClick(project.id)}>
                  <div className="card-header">{project.name}</div>
                  <div className="card-body">{project.description}</div>
                </div>
              ))
            ) : (
              <p>프로젝트가 없습니다.</p>
            )}
          </div>
        </section>

        <section>
          <h3>최근 작업</h3>
          {Object.keys(groupedTasks).map((date) => (
            <div key={date} className="task-date-group">
              <small className="task-date">{date}</small>
              <div className="task-list">
                {groupedTasks[date].map((task) => (
                  <div
                    key={task.id}
                    className="task-item"
                    onClick={() => handleTaskClick(task.projectId, task.boardId, task.id)}
                  >
                    <div>
                      <span className="task-title">{task.title}</span>
                      <div className="task-project-name">{task.projectName}</div>
                    </div>
                    <span className="home-task-status">{task.status}</span>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </section>
      </div>
    </div>
  );
};

export default Home;
