import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { taskService } from '../../services/TaskService';
import CommentSection from './CommentSection'; 

const TaskDetails = () => {
  const { taskId } = useParams(); 
  const navigate = useNavigate();
  const [task, setTask] = useState(null);

  useEffect(() => {
    const fetchTaskDetails = async () => {
      try {
        const taskData = await taskService.getTaskDetails(taskId);
        console.log(taskData);
        setTask(taskData);
      } catch (error) {
        console.error('테스크 정보를 불러오는 중 오류 발생:', error);
      }
    };

    fetchTaskDetails();
  }, [taskId]);

  const handleDeleteTask = async () => {
    if (window.confirm('정말 이 테스크를 삭제하시겠습니까?')) {
      try {
        await taskService.deleteTask(taskId); 
        alert('테스크가 삭제되었습니다.');
        navigate('/projects');
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
      <h2>{task.title}</h2>
      <p>설명: {task.description}</p>
      <p>상태: {task.status}</p>
      <p>담당자: {task.userName}</p>

      <button onClick={() => console.log('테스크 편집 클릭')}>테스크 편집</button>
      <button onClick={handleDeleteTask}>테스크 삭제</button>

      <CommentSection taskId={taskId} />
    </div>
  );
};

export default TaskDetails;
