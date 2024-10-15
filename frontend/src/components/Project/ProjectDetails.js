import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { projectService } from '../../services/ProjectService';
import { boardService } from '../../services/BoardService';
import { taskService } from '../../services/TaskService';
import BoardColumn from '../Board/BoardColumn';
import AddUserModal from './AddUserModal';
import UserModal from './UserModal';
import BoardModal from '../Board/BoardModal'; // 보드 추가 모달
import TaskModal from '../Task/TaskModal'; // 테스크 추가 모달

const ProjectDetails = () => {
  const { id } = useParams(); // 프로젝트 ID
  const navigate = useNavigate();
  const [project, setProject] = useState({});
  const [boards, setBoards] = useState([]);
  const [tasks, setTasks] = useState([]);
  const [isUserModalOpen, setIsUserModalOpen] = useState(false);
  const [isAddUserModalOpen, setIsAddUserModalOpen] = useState(false);
  const [isBoardModalOpen, setIsBoardModalOpen] = useState(false);
  const [isTaskModalOpen, setIsTaskModalOpen] = useState(false);
  const [selectedBoardId, setSelectedBoardId] = useState(null); // 테스크 추가 시 선택된 보드
  const [error, setError] = useState(null); // 에러 상태 관리


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
          setError('프로젝트에 대한 접근 권한이 없습니다.'); // 403 에러 메시지 설정
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
    navigate(`/tasks/${task.id}`); // 테스크 상세 페이지로 이동
  };

  const handleBoardCreated = newBoard => {
    setBoards(prevBoards => [...prevBoards, newBoard]); // 보드 추가 후 리스트 갱신
  };

  const handleTaskCreated = newTask => {
    setTasks(prevTasks => [...prevTasks, newTask]); // 테스크 추가 후 리스트 갱신
  };

  const handleAddTask = boardId => {
    setSelectedBoardId(boardId); // 보드 선택
    setIsTaskModalOpen(true); // 테스크 추가 모달 열기
  };

  if (error) {
    return <div>{error}</div>; // 에러 발생 시 에러 메시지 렌더링
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
            onAddTask={handleAddTask} // 테스크 추가 버튼 클릭 시 호출
          />
        ))}
      </div>

      <button onClick={() => setIsBoardModalOpen(true)}>보드 추가</button> {/* 보드 추가 버튼 */}
      <button onClick={() => console.log('프로젝트 삭제')}>프로젝트 삭제</button>
      <button onClick={() => setIsUserModalOpen(true)}>사용자 목록 보기</button>
      <button onClick={() => setIsAddUserModalOpen(true)}>사용자 추가</button>

      {/* 사용자 목록 모달 */}
      {isUserModalOpen && (
        <UserModal projectId={id} onClose={() => setIsUserModalOpen(false)} />
      )}

      {/* 사용자 추가 모달 */}
      {isAddUserModalOpen && <AddUserModal projectId={id} onClose={() => setIsAddUserModalOpen(false)} />}

      {/* 보드 추가 모달 */}
      {isBoardModalOpen && <BoardModal projectId={id} onClose={() => setIsBoardModalOpen(false)} onBoardCreated={handleBoardCreated} />}

      {/* 테스크 추가 모달 */}
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
