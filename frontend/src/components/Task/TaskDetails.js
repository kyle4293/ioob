import React, { useEffect, useState } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import { taskService } from '../../services/TaskService';
import CommentSection from './CommentSection';
import dayjs from 'dayjs';

const TaskDetails = () => {
  const { projectId, boardId, taskId } = useParams();
  const { state } = useLocation();
  const navigate = useNavigate();
  const [task, setTask] = useState(state?.task || null);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [updatedTitle, setUpdatedTitle] = useState('');
  const [updatedDescription, setUpdatedDescription] = useState('');
  const [updatedStatus, setUpdatedStatus] = useState('');

  useEffect(() => {
    if (!task) {
      const fetchTaskDetails = async () => {
        try {
          const taskData = await taskService.getTaskDetails(projectId, boardId, taskId);
          setTask(taskData);
          setUpdatedTitle(taskData.title);
          setUpdatedDescription(taskData.description);
          setUpdatedStatus(taskData.status);
        } catch (error) {
          console.error('테스크 정보를 불러오는 중 오류 발생:', error);
        }
      };

      fetchTaskDetails();
    } else {
      setUpdatedTitle(task.title);
      setUpdatedDescription(task.description);
      setUpdatedStatus(task.status);
    }
  }, [task, projectId, boardId, taskId]);

  const handleUpdateTask = async () => {
    try {
      const updatedTask = {
        title: updatedTitle,
        description: updatedDescription,
        status: updatedStatus,
      };
      const response = await taskService.updateTask(projectId, boardId, taskId, updatedTask);
      setTask(response);
      setIsEditModalOpen(false);
      alert('테스크가 수정되었습니다.');
    } catch (error) {
      console.error('테스크 수정 중 오류 발생:', error);
      alert('테스크 수정 중 오류가 발생했습니다.');
    }
  };

  const handleDeleteTask = async () => {
    if (window.confirm('정말 이 테스크를 삭제하시겠습니까?')) {
      try {
        await taskService.deleteTask(projectId, boardId, taskId);
        alert('테스크가 삭제되었습니다.');
        navigate(`/projects/${projectId}`);
      } catch (error) {
        console.error('테스크 삭제 중 오류 발생:', error);
        alert('테스크 삭제 권한이 없습니다.');
      }
    }
  };

  const openEditModal = () => {
    setIsEditModalOpen(true);
  };

  const closeEditModal = () => {
    setIsEditModalOpen(false);
  };

  if (!task) {
    return <div>로딩 중...</div>;
  }

  const createdAt = dayjs(task.createdAt).format('YYYY-MM-DD');
  const modifiedAt = dayjs(task.modifiedAt).format('YYYY-MM-DD');

  return (
    <div className="task-details">
      <div className="task-header">
        <div className="task-info">
          <h2>{task.title}</h2>
          <p className="task-project-name">{task.projectName}</p>
          <p className="task-dates">
            생성일: {createdAt} | 수정일: {modifiedAt}
          </p>
        </div>
        <div className="task-actions">
          <button onClick={openEditModal}>테스크 편집</button>
          <button onClick={handleDeleteTask}>테스크 삭제</button>
        </div>
      </div>

      <div className="task-content">
        <div className="task-description">
          <h3>설명</h3>
          <p>{task.description}</p>
        </div>
        <div className="task-information">
          <h3>상태</h3>
          <p>{task.status}</p>

          <h3>담당자</h3>
          <p>{task.userName || '담당자 없음'}</p>
        </div>
      </div>

      <CommentSection projectId={projectId} boardId={boardId} taskId={taskId} />

      {isEditModalOpen && (
        <div className="modal">
          <div className="modal-content">
            <h3>테스크 수정</h3>
            <input
              type="text"
              placeholder="제목"
              value={updatedTitle}
              onChange={(e) => setUpdatedTitle(e.target.value)}
            />
            <textarea
              placeholder="설명"
              value={updatedDescription}
              onChange={(e) => setUpdatedDescription(e.target.value)}
            />
            <select
              value={updatedStatus}
              onChange={(e) => setUpdatedStatus(e.target.value)}
            >
              <option value="TODO">TODO</option>
              <option value="IN_PROGRESS">IN_PROGRESS</option>
              <option value="DONE">DONE</option>
            </select>
            <div className="modal-actions">
              <button onClick={handleUpdateTask}>저장</button>
              <button onClick={closeEditModal}>취소</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default TaskDetails;
