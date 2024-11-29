import React, { useState, useEffect } from 'react';
import { projectService } from '../../services/ProjectService';
import { taskService } from '../../services/TaskService';

const TaskModal = ({ projectId, boardId, onClose, onTaskCreated }) => {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [status, setStatus] = useState('TODO');
  const [assignedToEmail, setAssignedToEmail] = useState('');
  const [users, setUsers] = useState([]);
  const [files, setFiles] = useState([]); // 파일 상태
  const [previewUrls, setPreviewUrls] = useState([]); // 이미지 미리보기 URL 상태

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

  const handleFileChange = (e) => {
    const selectedFiles = Array.from(e.target.files);
    setFiles(selectedFiles);

    const imageFiles = selectedFiles.filter((file) =>
      file.type.startsWith('image/')
    );
    setPreviewUrls(imageFiles.map((file) => URL.createObjectURL(file)));
  };

  const handleCreateTask = async () => {
    try {
      const formData = new FormData();
      formData.append('title', title);
      formData.append('description', description);
      formData.append('status', status);
      formData.append('assignedToEmail', assignedToEmail);
      files.forEach((file) => formData.append('files', file)); 
  
      const newTask = await taskService.createTask(projectId, boardId, formData); 
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
        <br />
        <select
          value={assignedToEmail}
          onChange={(e) => setAssignedToEmail(e.target.value)}
        >
          <option value="">담당자 선택</option>
          {users.map((user) => (
            <option key={user.email} value={user.email}>
              {user.name} ({user.email})
            </option>
          ))}
        </select>
        <div className="file-upload">
          <input type="file" multiple onChange={handleFileChange} />
          <div className="file-preview">
            {previewUrls.map((url, index) => (
              <img key={index} src={url} alt={`preview-${index}`} />
            ))}
            {files
              .filter((file) => !file.type.startsWith('image/'))
              .map((file, index) => (
                <div key={index} className="file-name">
                  {file.name}
                </div>
              ))}
          </div>
        </div>
        <div className="modal-actions">
          <button onClick={handleCreateTask}>테스크 생성</button>
          <button onClick={onClose}>취소</button>
        </div>
      </div>
    </div>
  );
};

export default TaskModal;
