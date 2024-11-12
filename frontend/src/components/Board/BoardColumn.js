import React, { useState, useRef, useEffect } from 'react';
import TaskList from '../Task/TaskList';
import TaskModal from '../Task/TaskModal';
import { boardService } from '../../services/BoardService';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEllipsisV } from '@fortawesome/free-solid-svg-icons';

const BoardColumn = ({ projectId, board, onBoardUpdated, onBoardDeleted }) => {
  const [tasks, setTasks] = useState(board.tasks || []);
  const [isTaskModalOpen, setIsTaskModalOpen] = useState(false);
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [editedBoardName, setEditedBoardName] = useState(board.name);

  const dropdownRef = useRef(null);

  const handleOpenTaskModal = () => {
    setIsTaskModalOpen(true);
    setIsDropdownOpen(false); // 드롭다운 닫기
  };

  const handleCloseTaskModal = () => {
    setIsTaskModalOpen(false);
  };

  const handleTaskCreated = (newTask) => {
    setTasks((prevTasks) => [...prevTasks, newTask]);
    handleCloseTaskModal();
  };

  // 보드 이름 수정 처리
  const handleEditBoard = async () => {
    try {
      const updatedBoard = await boardService.editBoard(board.id, { name: editedBoardName });
      onBoardUpdated(updatedBoard);
      setIsEditing(false);
      setIsDropdownOpen(false);
    } catch (error) {
      console.error('보드 수정 중 오류 발생:', error);
    }
  };

  // 보드 삭제 처리
  const handleDeleteBoard = async () => {
    if (window.confirm('정말 이 보드를 삭제하시겠습니까?')) {
      try {
        await boardService.deleteBoard(board.id);
        onBoardDeleted(board.id);
        setIsDropdownOpen(false);
      } catch (error) {
        console.error('보드 삭제 중 오류 발생:', error);
      }
    }
  };

  // 드롭다운 열기/닫기
  const toggleDropdown = () => {
    setIsDropdownOpen((prev) => !prev);
  };

  // 외부 클릭 감지하여 드롭다운 닫기
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

  return (
    <div className="board-column">
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
                <div className="dropdown-menu">
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
