import React, { useState, useRef, useEffect } from 'react';
import { useDrag, useDrop } from 'react-dnd';
import TaskList from '../Task/TaskList';
import TaskModal from '../Task/TaskModal';
import { boardService } from '../../services/BoardService';
import { taskService } from '../../services/TaskService';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEllipsisV } from '@fortawesome/free-solid-svg-icons';

const BoardColumn = ({ projectId, board, boardOrder, moveBoard, saveBoardOrder, onBoardUpdate }) => {
  const [tasks, setTasks] = useState([]);
  const [isTaskModalOpen, setIsTaskModalOpen] = useState(false);
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [editedBoardName, setEditedBoardName] = useState(board.name);

  const dropdownRef = useRef(null);

  useEffect(() => {
    const fetchTasks = async () => {
      try {
        const taskData = await taskService.getTasksByBoardId(projectId, board.id);
        setTasks(taskData);
      } catch (error) {
        console.error('테스크 목록을 불러오는 중 오류:', error);
      }
    };

    fetchTasks();
  }, [projectId, board.id]);

  const handleOpenTaskModal = () => {
    setIsTaskModalOpen(true);
    setIsDropdownOpen(false);
  };

  const handleCloseTaskModal = () => {
    setIsTaskModalOpen(false);
  };

  const handleTaskCreated = (newTask) => {
    setTasks((prevTasks) => [...prevTasks, newTask]);
    handleCloseTaskModal();
    onBoardUpdate();
  };

  const handleEditBoard = async () => {
    try {
      await boardService.editBoard(projectId, board.id, { name: editedBoardName });
      setIsEditing(false);
      setIsDropdownOpen(false);
      onBoardUpdate();
    } catch (error) {
      console.error('보드 수정 중 오류 발생:', error);
    }
  };

  const handleDeleteBoard = async () => {
    if (window.confirm('정말 이 보드를 삭제하시겠습니까?')) {
      try {
        await boardService.deleteBoard(projectId, board.id);
        setIsDropdownOpen(false);
        onBoardUpdate();
      } catch (error) {
        console.error('보드 삭제 중 오류 발생:', error);
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

  const ref = useRef(null);

  const [{ isDragging }, drag] = useDrag({
    type: 'BOARD',
    item: { boardOrder, boardId: board.id },
    collect: (monitor) => ({
      isDragging: monitor.isDragging(),
    }),
  });

  const [, dropBoard] = useDrop({
    accept: 'BOARD',
    hover(item) {
      if (item.boardOrder !== boardOrder) {
        moveBoard(item.boardOrder, boardOrder);
        item.boardOrder = boardOrder;
      }
    },
    drop: saveBoardOrder,
  });

  drag(dropBoard(ref));

  const [, dropTask] = useDrop({
    accept: 'TASK',
    drop: async (item) => {
      try {
        const updatedTask = await taskService.moveTaskToBoard(projectId, board.id, item.task.id);
        setTasks((prevTasks) => [...prevTasks.filter((task) => task.id !== item.task.id), updatedTask]);
        onBoardUpdate();
      } catch (error) {
        console.error('테스크 이동 중 오류 발생:', error);
      }
    },
  });

  return (
    <div
      ref={(el) => {
        ref.current = el;
        dropTask(el);
      }}
      className="board-column"
      style={{ backgroundColor: '#f1f1f1', opacity: isDragging ? 0.5 : 1 }}
    >
      <div className="board-header">
        {isEditing ? (
          <>
            <input
              type="text"
              value={editedBoardName}
              onChange={(e) => setEditedBoardName(e.target.value)}
              placeholder="보드 이름"
            />
            <button onClick={handleEditBoard}>저장</button>
            <button onClick={() => setIsEditing(false)}>취소</button>
          </>
        ) : (
          <>
            <h3>{board.name}</h3>
            <div className="dropdown" ref={dropdownRef}>
              <button className="more-options-button" onClick={toggleDropdown}>
                <FontAwesomeIcon icon={faEllipsisV} />
              </button>
              {isDropdownOpen && (
                <div className="board-dropdown-menu">
                  <button onClick={handleOpenTaskModal}>테스크 추가</button>
                  <button onClick={() => setIsEditing(true)}>보드 수정</button>
                  <button onClick={handleDeleteBoard}>보드 삭제</button>
                </div>
              )}
            </div>
          </>
        )}
      </div>

      <TaskList tasks={tasks} />

      {isTaskModalOpen && (
        <TaskModal
          projectId={projectId}
          boardId={board.id}
          onClose={handleCloseTaskModal}
          onTaskCreated={handleTaskCreated}
        />
      )}
    </div>
  );
};

export default BoardColumn;
