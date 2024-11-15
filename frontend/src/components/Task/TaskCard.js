import React from 'react';
import { useDrag } from 'react-dnd';

const TaskCard = ({ task, onMove, onClick }) => {
  const [{ isDragging }, drag] = useDrag({
    type: 'TASK',
    item: { task },
    end: (item, monitor) => {
      const didDrop = monitor.didDrop();
      if (didDrop && onMove) {
        onMove(item.task.id); 
      }
    },
    collect: (monitor) => ({
      isDragging: monitor.isDragging(),
    }),
  });

  return (
    <div
      ref={drag}
      className="task-card"
      style={{ opacity: isDragging ? 0.5 : 1 }}
      onClick={onClick}
    >
      <div className="task-title">{task.title}</div>
      <div className="task-info">
        <div className="task-status">{task.status}</div>
        <div className="task-user">{task.assignedToName || 'Unassigned'}</div>
      </div>
    </div>
  );
};

export default TaskCard;
