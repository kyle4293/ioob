import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { taskService } from '../../services/TaskService';
import CommentSection from './CommentSection'; // 댓글 섹션 컴포넌트

const TaskDetails = () => {
  const { taskId } = useParams(); // URL에서 테스크 ID 가져옴
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
      <button onClick={() => console.log('테스크 삭제 클릭')}>테스크 삭제</button>

      {/* 댓글 섹션 추가 */}
      <CommentSection taskId={taskId} />
    </div>
  );
};

export default TaskDetails;
