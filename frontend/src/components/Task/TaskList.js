import React from 'react';
import { useNavigate } from 'react-router-dom';

const TaskList = ({ tasks }) => {
  const navigate = useNavigate();

  const handleTaskClick = (task) => {
    navigate(`/projects/${task.projectId}/boards/${task.boardId}/tasks/${task.id}`, { state: { task } });
  };  

  return (
    <div className="task-list">
      {tasks.length > 0 ? (
        tasks.map((task) => (
          <div key={task.id} className="task-card" onClick={() => handleTaskClick(task)}>
            <div className="task-title">{task.title}</div>
            <div className="task-details">
              <div className="task-status">{task.status}</div>
              <div className="task-user">{task.userName || 'Unassigned'}</div>
            </div>
          </div>
        ))
      ) : (
        <p>테스크가 없습니다.</p>
      )}
    </div>
  );
};

export default TaskList;
