import React, { useEffect, useState, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { projectService } from '../../services/ProjectService';
import BoardList from '../Board/BoardList';
import AddUserModal from './AddUserModal';
import ProjectUserList from './ProjectUserList';
import EditProjectModal from './EditProjectModal';
import BoardModal from '../Board/BoardModal';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEllipsisV } from '@fortawesome/free-solid-svg-icons';
import dayjs from 'dayjs';

const ProjectDetails = () => {
  const { id: projectId } = useParams();
  const navigate = useNavigate();
  const [project, setProject] = useState({});
  const [updatedProjectName, setUpdatedProjectName] = useState(project.name || '');
  const [updatedProjectDescription, setUpdatedProjectDescription] = useState(project.description || '');
  const [isUserModalOpen, setIsUserModalOpen] = useState(false);
  const [isAddUserModalOpen, setIsAddUserModalOpen] = useState(false);
  const [isBoardModalOpen, setIsBoardModalOpen] = useState(false);
  const [isEditProjectModalOpen, setIsEditProjectModalOpen] = useState(false);
  const dropdownRef = useRef(null);
  const onBoardUpdateRef = useRef(null);
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchProjectDetails = async () => {
      try {
        const projectData = await projectService.getProjectDetails(projectId);
        setProject(projectData);
        setUpdatedProjectName(projectData.name);
        setUpdatedProjectDescription(projectData.description);
      } catch (error) {
        console.error('프로젝트 정보를 불러오는 중 오류:', error);
        setError('프로젝트 정보를 불러오는 중 오류가 발생했습니다.');
      }
    };

    fetchProjectDetails();
  }, [projectId]);

  const handleBoardCreated = () => {
    if (onBoardUpdateRef.current) {
      onBoardUpdateRef.current(); 
    }
  };

  const handleProjectUpdate = async () => {
    try {
      await projectService.updateProject(projectId, { name: updatedProjectName, description: updatedProjectDescription });
      setProject((prev) => ({
        ...prev,
        name: updatedProjectName,
        description: updatedProjectDescription,
      }));
      setIsEditProjectModalOpen(false);
      alert('프로젝트가 업데이트되었습니다.');
    } catch (error) {
      console.error('프로젝트 업데이트 중 오류 발생:', error);
      alert('프로젝트 업데이트 권한이 없습니다.');
    }
  };

  const handleDeleteProject = async () => {
    if (window.confirm('정말 이 프로젝트를 삭제하시겠습니까?')) {
      try {
        await projectService.deleteProject(projectId);
        alert('프로젝트가 삭제되었습니다.');
        navigate('/');
      } catch (error) {
        console.error('프로젝트 삭제 중 오류 발생:', error);
        alert('프로젝트 삭제 권한이 없습니다.');
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

  if (error) {
    return <div>{error}</div>;
  }

  const createdAt = dayjs(project.createdAt).format('YYYY-MM-DD');

  return (
    <div className="project-details">
      <div className="project-details-header">
        <div>
          <h2>{project.name}</h2>
          <p className="project-info">{project.description} / 생성: {createdAt}</p>
        </div>

        <button className="project-dropdown-button" onClick={toggleDropdown}>
          <FontAwesomeIcon icon={faEllipsisV} />
        </button>
        {isDropdownOpen && (
          <div className="project-dropdown-menu" ref={dropdownRef}>
            <button onClick={() => setIsBoardModalOpen(true)}>보드 추가</button>
            <button onClick={() => setIsEditProjectModalOpen(true)}>프로젝트 수정</button>
            <button onClick={handleDeleteProject}>프로젝트 삭제</button>
            <button onClick={() => setIsUserModalOpen(true)}>사용자 목록 보기</button>
            <button onClick={() => setIsAddUserModalOpen(true)}>사용자 추가</button>
          </div>
        )}
      </div>

      <BoardList projectId={projectId} onBoardUpdateRef={onBoardUpdateRef} />

      {isUserModalOpen && (
        <ProjectUserList projectId={projectId} onClose={() => setIsUserModalOpen(false)} />
      )}

      {isAddUserModalOpen && (
        <AddUserModal projectId={projectId} onClose={() => setIsAddUserModalOpen(false)} />
      )}

      {isBoardModalOpen && (
        <BoardModal
          projectId={projectId}
          onClose={() => setIsBoardModalOpen(false)}
          onBoardCreated={handleBoardCreated}
        />
      )}

      {isEditProjectModalOpen && (
        <EditProjectModal
          projectName={updatedProjectName}
          projectDescription={updatedProjectDescription}
          onClose={() => setIsEditProjectModalOpen(false)}
          onSave={handleProjectUpdate}
          setProjectName={setUpdatedProjectName}
          setProjectDescription={setUpdatedProjectDescription}
        />
      )}
    </div>
  );
};

export default ProjectDetails;
