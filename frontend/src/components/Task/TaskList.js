import React from 'react';

const TaskList = ({ tasks, onTaskClick }) => {
  return (
    <div className="task-list">
      {tasks.length > 0 ? (
        tasks.map(task => (
          <div key={task.id} className="task-card" onClick={() => onTaskClick(task)}>
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
