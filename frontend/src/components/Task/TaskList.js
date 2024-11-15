import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import TaskCard from './TaskCard';

const TaskList = ({ tasks, onTaskMove }) => {
  const navigate = useNavigate();
  const [taskList, setTaskList] = useState(tasks);

  useEffect(() => {
    setTaskList(tasks);
  }, [tasks]);

  const handleTaskClick = (task) => {
    navigate(`/projects/${task.projectId}/boards/${task.boardId}/tasks/${task.id}`, { state: { task } });
  };  

  const handleTaskMove = (taskId) => {
    setTaskList((prevTasks) => prevTasks.filter((task) => task.id !== taskId));
    if (onTaskMove) onTaskMove(taskId); 
  };

  return (
    <div className="task-list">
      {taskList.length > 0 ? (
        taskList.map((task) => (
          <TaskCard key={task.id} task={task} onMove={handleTaskMove} onClick={() => handleTaskClick(task)}/>
        ))
      ) : (
        <p>테스크가 없습니다.</p>
      )}
    </div>
  );
};

export default TaskList;
