import React, { useEffect, useState } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import { taskService } from '../../services/TaskService';
import CommentSection from './CommentSection';

const TaskDetails = () => {
  const { projectId, boardId, taskId } = useParams();
  const { state } = useLocation();
  const navigate = useNavigate();
  const [task, setTask] = useState(state?.task || null);
  const [isEditing, setIsEditing] = useState(false);
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
      setIsEditing(false);
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

  if (!task) {
    return <div>로딩 중...</div>;
  }

  return (
    <div className="task-details">
      <p>{task.projectName}</p>
      <h2>{isEditing ? <input type="text" value={updatedTitle} onChange={(e) => setUpdatedTitle(e.target.value)} /> : task.title}</h2>
      <p>설명: {isEditing ? <textarea value={updatedDescription} onChange={(e) => setUpdatedDescription(e.target.value)} /> : task.description}</p>
      <p>상태: {isEditing ? (
        <select value={updatedStatus} onChange={(e) => setUpdatedStatus(e.target.value)}>
          <option value="TODO">TODO</option>
          <option value="IN_PROGRESS">IN_PROGRESS</option>
          <option value="DONE">DONE</option>
        </select>
      ) : task.status}</p>
      <p>담당자: {task.userName}</p>

      {isEditing ? (
        <>
          <button onClick={handleUpdateTask}>저장</button>
          <button onClick={() => setIsEditing(false)}>취소</button>
        </>
      ) : (
        <button onClick={() => setIsEditing(true)}>테스크 편집</button>
      )}
      <button onClick={handleDeleteTask}>테스크 삭제</button>

      <CommentSection projectId={projectId} boardId={boardId} taskId={taskId} />
    </div>
  );
};

export default TaskDetails;
