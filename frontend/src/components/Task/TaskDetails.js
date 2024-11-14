import React, { useEffect, useState, useRef } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import { taskService } from '../../services/TaskService';
import { projectService } from '../../services/ProjectService';
import { boardService } from '../../services/BoardService';
import CommentSection from './CommentSection';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEllipsisV } from '@fortawesome/free-solid-svg-icons';
import dayjs from 'dayjs';

const TaskDetails = () => {
  const { projectId, boardId, taskId } = useParams();
  const { state } = useLocation();
  const navigate = useNavigate();
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [task, setTask] = useState(state?.task || null);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [updatedTitle, setUpdatedTitle] = useState('');
  const [updatedDescription, setUpdatedDescription] = useState('');
  const [updatedStatus, setUpdatedStatus] = useState('');
  const [assignedToEmail, setAssignedToEmail] = useState('');
  const [updatedBoardId, setUpdatedBoardId] = useState(boardId);
  const [users, setUsers] = useState([]);
  const [boards, setBoards] = useState([]);

  const dropdownRef = useRef(null);

  useEffect(() => {
    if (!task) {
      const fetchTaskDetails = async () => {
        try {
          const taskData = await taskService.getTaskDetails(projectId, boardId, taskId);
          setTask(taskData);
          setUpdatedTitle(taskData.title);
          setUpdatedDescription(taskData.description);
          setUpdatedStatus(taskData.status);
          setAssignedToEmail(taskData.assignedToEmail || '');
          setUpdatedBoardId(taskData.boardId);
        } catch (error) {
          console.error('테스크 정보를 불러오는 중 오류 발생:', error);
        }
      };
      fetchTaskDetails();
    } else {
      setUpdatedTitle(task.title);
      setUpdatedDescription(task.description);
      setUpdatedStatus(task.status);
      setAssignedToEmail(task.assignedToEmail || '');
      setUpdatedBoardId(task.boardId);
    }
  }, [task, projectId, boardId, taskId]);

  useEffect(() => {
    const fetchProjectData = async () => {
      try {
        const userData = await projectService.getUsersInProject(projectId);
        setUsers(userData);
        const boardData = await boardService.getBoards(projectId);
        setBoards(boardData);
      } catch (error) {
        console.error('프로젝트 정보 불러오기 오류:', error);
      }
    };
    fetchProjectData();
  }, [projectId]);

  const handleUpdateTask = async () => {
    try {
      const updatedTask = {
        title: updatedTitle,
        description: updatedDescription,
        status: updatedStatus,
        assignedToEmail,
        boardId: updatedBoardId,
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

  const toggleDropdown = () => {
    setIsDropdownOpen((prev) => !prev);
  };

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsDropdownOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  const openEditModal = () => {
    setIsEditModalOpen(true);
  };

  const closeEditModal = () => {
    setIsEditModalOpen(false);
  };

  if (!task) {
    return <div>로딩 중...</div>;
  }

  const createdAt = task.createdAt ? dayjs(task.createdAt).format('YYYY-MM-DD') : '';
  const modifiedAt = task.modifiedAt ? dayjs(task.modifiedAt).format('YYYY-MM-DD') : '';

  return (
    <div className="task-details">
      <p className="task-project-name">
        <span onClick={() => navigate(`/projects/${task.projectId}`)}>
          {task.projectName}
        </span>
        {' / '}
        {task.boardName}
      </p>
      <div className="task-header">
        <div className="task-project-info">
          <h2>{task.title}</h2>
          <p className="task-dates">
            생성일: {createdAt} | 수정일: {modifiedAt}
          </p>
        </div>

        <button className="task-dropdown-button" onClick={toggleDropdown}>
          <FontAwesomeIcon icon={faEllipsisV} />
        </button>
        {isDropdownOpen && (
          <div className="task-dropdown-menu" ref={dropdownRef}>
            <button className="task-edit" onClick={openEditModal}>편집</button>
            <button className="task-delete" onClick={handleDeleteTask}>삭제</button>
          </div>
        )}

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
          <p>{task.assignedToName || 'Unassigned'}</p>
          <h3>생성자</h3>
          <p>{task.createdByName}</p>
        </div>
      </div>

      <CommentSection projectId={projectId} boardId={boardId} taskId={taskId} />

      {isEditModalOpen && (
        <div className="modal">
          <div className="modal-content">
            <h3>테스크 수정</h3>
        
            <div className="modal-row">
              <label className="modal-label">제목</label>
              <input
                type="text"
                className="modal-input"
                placeholder="제목"
                value={updatedTitle}
                onChange={(e) => setUpdatedTitle(e.target.value)}
              />
            </div>
        
            <div className="modal-row">
              <label className="modal-label">설명</label>
              <textarea
                className="modal-input"
                placeholder="설명"
                value={updatedDescription}
                onChange={(e) => setUpdatedDescription(e.target.value)}
              />
            </div>
        
            <div className="modal-row">
              <label className="modal-label">상태</label>
              <select
                className="modal-input"
                value={updatedStatus}
                onChange={(e) => setUpdatedStatus(e.target.value)}
              >
                <option value="TODO">TODO</option>
                <option value="IN_PROGRESS">IN_PROGRESS</option>
                <option value="DONE">DONE</option>
              </select>
            </div>
        
            <div className="modal-row">
              <label className="modal-label">담당자</label>
              <select
                className="modal-input"
                value={assignedToEmail}
                onChange={(e) => setAssignedToEmail(e.target.value)}
              >
                {users.map((user) => (
                  <option key={user.userEmail} value={user.userEmail}>
                    {user.userName} ({user.userEmail})
                  </option>
                ))}
              </select>
            </div>
        
            <div className="modal-row">
              <label className="modal-label">보드</label>
              <select
                className="modal-input"
                value={updatedBoardId}
                onChange={(e) => setUpdatedBoardId(e.target.value)}
              >
                {boards.map((board) => (
                  <option key={board.id} value={board.id}>
                    {board.name}
                  </option>
                ))}
              </select>
            </div>
        
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
