import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { projectService } from '../../services/ProjectService';
import { boardService } from '../../services/BoardService';
import { taskService } from '../../services/TaskService';
import BoardColumn from '../Board/BoardColumn';
import AddUserModal from './AddUserModal';
import UserModal from './UserModal';
import BoardModal from '../Board/BoardModal'; 
import TaskModal from '../Task/TaskModal';

const ProjectDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [project, setProject] = useState({});
  const [boards, setBoards] = useState([]);
  const [tasks, setTasks] = useState([]);
  const [isUserModalOpen, setIsUserModalOpen] = useState(false);
  const [isAddUserModalOpen, setIsAddUserModalOpen] = useState(false);
  const [isBoardModalOpen, setIsBoardModalOpen] = useState(false);
  const [isTaskModalOpen, setIsTaskModalOpen] = useState(false);
  const [selectedBoardId, setSelectedBoardId] = useState(null);
  const [error, setError] = useState(null); 


  useEffect(() => {
    const fetchProjectDetails = async () => {
      try {
        const projectData = await projectService.getProjectDetails(id);
        setProject(projectData);

        const boardData = await boardService.getBoards(id);
        setBoards(boardData);
      } catch (error) {
        console.log(error);
        if (error.response && error.response.status === 403) {
          setError('프로젝트에 대한 접근 권한이 없습니다.'); 
        } else {
          setError('프로젝트 정보를 불러오는 중 오류가 발생했습니다.');
        }
      }
    };

    fetchProjectDetails();
  }, [id]);

  useEffect(() => {
    if (boards.length > 0) {
      const fetchTasks = async () => {
        try {
          const tasksForBoards = await Promise.all(
            boards.map(board => taskService.getTasks(board.id))
          );
          setTasks(tasksForBoards.flat());
        } catch (error) {
          console.error('테스크 정보를 불러오는 중 오류가 발생했습니다:', error);
        }
      };

      fetchTasks();
    }
  }, [boards]);

  const handleTaskClick = task => {
    navigate(`/tasks/${task.id}`); 
  };

  const handleBoardCreated = newBoard => {
    setBoards(prevBoards => [...prevBoards, newBoard]); 
  };

  const handleTaskCreated = newTask => {
    setTasks(prevTasks => [...prevTasks, newTask]); 
  };

  const handleAddTask = boardId => {
    setSelectedBoardId(boardId); 
    setIsTaskModalOpen(true); 
  };

  const handleDeleteProject = async () => {
    if (window.confirm('정말 이 프로젝트를 삭제하시겠습니까?')) {
      try {
        await projectService.deleteProject(id); 
        alert('프로젝트가 삭제되었습니다.');
        navigate('/projects');
      } catch (error) {
        console.error('프로젝트 삭제 중 오류 발생:', error);
        alert('프로젝트 삭제 권한이 없습니다.');
      }
    }
  };

  if (error) {
    return <div>{error}</div>;
  }

  return (
    <div className="project-details">
      <h2>{project.name}</h2>
      <p>{project.description}</p>

      <div className="kanban-container">
        {boards.map(board => (
          <BoardColumn
            key={board.id}
            board={board}
            tasks={tasks.filter(task => task.boardId === board.id)}
            onTaskClick={handleTaskClick}
            onAddTask={handleAddTask} 
          />
        ))}
      </div>

      <button onClick={() => setIsBoardModalOpen(true)}>보드 추가</button> 
      <button onClick={handleDeleteProject}>프로젝트 삭제</button>
      <button onClick={() => setIsUserModalOpen(true)}>사용자 목록 보기</button>
      <button onClick={() => setIsAddUserModalOpen(true)}>사용자 추가</button>

      {isUserModalOpen && (
        <UserModal projectId={id} onClose={() => setIsUserModalOpen(false)} />
      )}

      {isAddUserModalOpen && <AddUserModal projectId={id} onClose={() => setIsAddUserModalOpen(false)} />}

      {isBoardModalOpen && <BoardModal projectId={id} onClose={() => setIsBoardModalOpen(false)} onBoardCreated={handleBoardCreated} />}

      {isTaskModalOpen && (
        <TaskModal
          boardId={selectedBoardId}
          onClose={() => setIsTaskModalOpen(false)}
          onTaskCreated={handleTaskCreated}
        />
      )}
    </div>
  );
};

export default ProjectDetails;
