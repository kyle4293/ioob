import React, { useState } from 'react';
import TaskList from '../Task/TaskList';
import { boardService } from '../../services/BoardService';

const BoardColumn = ({ board, tasks, onTaskClick, onAddTask }) => {
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [editedBoardName, setEditedBoardName] = useState(board.name);

  const toggleDropdown = () => {
    setIsDropdownOpen(!isDropdownOpen);
  };

  // 보드 편집 기능
  const handleEditBoard = async () => {
    try {
      await boardService.editBoard(board.id, { name: editedBoardName });
      setIsEditing(false);
      console.log('보드가 수정되었습니다.');
    } catch (error) {
      console.error('보드 편집 중 오류 발생:', error);
    }
  };

  // 보드 삭제 기능
  const handleDeleteBoard = async () => {
    if (window.confirm('정말 이 보드를 삭제하시겠습니까?')) {
      try {
        await boardService.deleteBoard(board.id);
        console.log('보드가 삭제되었습니다.');
      } catch (error) {
        console.error('보드 삭제 중 오류 발생:', error);
      }
    }
  };

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
            {board.name}
            <button onClick={toggleDropdown} className="board-menu-button">
              관리
            </button>
            {isDropdownOpen && (
              <div className="board-dropdown">
                <button onClick={() => setIsEditing(true)}>보드 편집</button>
                <button onClick={handleDeleteBoard}>보드 삭제</button>
                <button onClick={() => onAddTask(board.id)}>테스크 추가</button>
              </div>
            )}
          </>
        )}
      </div>

      <TaskList tasks={tasks} onTaskClick={onTaskClick} />
    </div>
  );
};

export default BoardColumn;
