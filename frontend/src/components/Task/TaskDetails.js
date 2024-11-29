import React, { useEffect, useState, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { taskService } from '../../services/TaskService';
import { projectService } from '../../services/ProjectService';
import { boardService } from '../../services/BoardService';
import CommentSection from './CommentSection';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEllipsisV } from '@fortawesome/free-solid-svg-icons';
import dayjs from 'dayjs';

const TaskDetails = () => {
  const { projectId, boardId, taskId } = useParams();
  const navigate = useNavigate();
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [task, setTask] = useState(null);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [updatedTitle, setUpdatedTitle] = useState('');
  const [updatedDescription, setUpdatedDescription] = useState('');
  const [updatedStatus, setUpdatedStatus] = useState('');
  const [assignedToEmail, setAssignedToEmail] = useState('');
  const [updatedBoardId, setUpdatedBoardId] = useState(boardId);
  const [users, setUsers] = useState([]);
  const [boards, setBoards] = useState([]);

  const [isModalOpen, setIsModalOpen] = useState(false); 
  const [selectedImage, setSelectedImage] = useState(null); 

  const dropdownRef = useRef(null);

  useEffect(() => {
    const fetchTaskDetails = async () => {
      try {
        const taskData = await taskService.getTaskDetails(projectId, boardId, taskId);
        setTask(taskData);
        console.log(taskData);
        setUpdatedTitle(taskData.title);
        setUpdatedDescription(taskData.description);
        setUpdatedStatus(taskData.status);
        setAssignedToEmail(taskData.assignedTo.email);
        setUpdatedBoardId(taskData.board.id);
      } catch (error) {
        console.error('테스크 정보를 불러오는 중 오류 발생:', error);
      }
    };
    fetchTaskDetails();
    
  }, [projectId, boardId, taskId]);

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
        assignedToEmail: assignedToEmail,
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

  const handleFileDownload = async (fileId, fileName) => {
    try {
      const fileData = await taskService.downloadFile(fileId);
  
      const url = window.URL.createObjectURL(new Blob([fileData]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', fileName); 
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      console.error('파일 다운로드 중 오류 발생:', error);
      alert('파일 다운로드에 실패했습니다.');
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

  const openImageModal = (imageUrl) => {
    setSelectedImage(imageUrl);
    setIsModalOpen(true);
  };

  const closeImageModal = () => {
    setIsModalOpen(false);
    setSelectedImage(null);
  };

  if (!task) {
    return <div>로딩 중...</div>;
  }

  const createdAt = task.createdAt ? dayjs(task.createdAt).format('YYYY-MM-DD') : '';
  const modifiedAt = task.modifiedAt ? dayjs(task.modifiedAt).format('YYYY-MM-DD') : '';

  return (
    <div className="task-details">
      <p className="task-project-name">
        <span onClick={() => navigate(`/projects/${projectId}`)}>
          {task.project.name} / {task.board.name}
        </span>
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

          {task.files &&
            task.files
              .filter((file) => file.fileType.startsWith('image/'))
              .map((file) => (
                <img
                  key={file.id}
                  src={file.previewUrl}
                  alt={file.fileName}
                  onClick={() => openImageModal(file.previewUrl)}
                />
              ))}

          {task.files && task.files.some((file) => !file.fileType.startsWith('image/')) && (
            <div className='task-files'>
              <h4>첨부 파일</h4>
              <ul>
                {task.files
                  .filter((file) => !file.fileType.startsWith('image/'))
                  .map((file) => (
                    <li key={file.id}>
                      <button
                        className="file-download-button"
                        onClick={() => handleFileDownload(file.id, file.fileName)}
                      >
                        {file.fileName}
                      </button>
                    </li>
                  ))}
              </ul>
            </div>
          )}
        </div>

        <div className="task-information">
          <h3>상태</h3>
          <p>{task.status}</p>
          <h3>담당자</h3>
          <p>{task.assignedTo ? task.assignedTo.name : 'Unassigned'}</p>
          <h3>생성자</h3>
          <p>{task.createdBy.name}</p>
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
              > <option value="">Unassigned</option>
                {users.map((user) => (
                  <option key={user.email} value={user.email}>
                    {user.name} ({user.email})
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

      {isModalOpen && selectedImage && (
        <div className="image-modal" onClick={closeImageModal}>
          <div className="image-modal-content">
            <img src={selectedImage} alt="확대 이미지" style={{ maxWidth: '90%', maxHeight: '90%' }} />
          </div>
        </div>
      )}
    </div>
  );
};

export default TaskDetails;
