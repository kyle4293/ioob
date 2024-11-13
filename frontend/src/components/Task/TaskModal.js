import React, { useState } from 'react';
import { taskService } from '../../services/TaskService';

const TaskModal = ({ projectId, boardId, onClose, onTaskCreated }) => {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [status, setStatus] = useState('TODO'); // 기본값은 'TODO'

  const handleCreateTask = async () => {
    try {
      const taskRequest = {
        title,
        description,
        status,
        boardId, 
      };
      const newTask = await taskService.createTask(projectId, boardId, taskRequest);
      onTaskCreated(newTask);
      onClose();
    } catch (error) {
      console.error('테스크 생성 중 오류 발생:', error);
    }
  };

  return (
    <div className="modal">
      <div className="modal-content">
        <h3>새 테스크 추가</h3>
        <input
          type="text"
          placeholder="테스크 제목"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
        />
        <textarea
          placeholder="테스크 설명"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
        <select value={status} onChange={(e) => setStatus(e.target.value)}>
          <option value="TODO">TODO</option>
          <option value="IN_PROGRESS">IN_PROGRESS</option>
          <option value="DONE">DONE</option>
        </select>
        <br></br>
        <button onClick={handleCreateTask}>테스크 생성</button>
        <button onClick={onClose}>취소</button>
      </div>
    </div>
  );
};

export default TaskModal;
