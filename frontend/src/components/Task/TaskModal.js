import React, { useState, useEffect } from 'react';
import { projectService } from '../../services/ProjectService';
import { taskService } from '../../services/TaskService';

const TaskModal = ({ projectId, boardId, onClose, onTaskCreated }) => {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [status, setStatus] = useState('TODO'); 
  const [assignedToEmail, setAssignedToEmail] = useState(''); 
  const [users, setUsers] = useState([]); 

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const userData = await projectService.getUsersInProject(projectId);
        setUsers(userData);
      } catch (error) {
        console.error('프로젝트 사용자 목록 불러오기 오류:', error);
      }
    };

    fetchUsers();
  }, [projectId]);

  const handleCreateTask = async () => {
    try {
      const taskRequest = {
        title,
        description,
        status,
        boardId, 
        assignedToEmail,
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
        <select
          value={assignedToEmail}
          onChange={(e) => setAssignedToEmail(e.target.value)}
        >
          <option value="">담당자 선택</option>
          {users.map((user) => (
            <option key={user.userEmail} value={user.userEmail}>
              {user.userName} ({user.userEmail})
            </option>
          ))}
        </select>
        <button onClick={handleCreateTask}>테스크 생성</button>
        <button onClick={onClose}>취소</button>
      </div>
    </div>
  );
};

export default TaskModal;
