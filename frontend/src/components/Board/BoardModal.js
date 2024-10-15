import React, { useState } from 'react';
import { boardService } from '../../services/BoardService';

const BoardModal = ({ projectId, onClose, onBoardCreated }) => {
  const [boardName, setBoardName] = useState('');

  const handleCreateBoard = async () => {
    try {
      const boardRequest = {
        name: boardName,
        projectId: projectId,
      };
      const newBoard = await boardService.createBoard(boardRequest);
      onBoardCreated(newBoard); // 보드가 생성되면 부모 컴포넌트에서 리스트를 갱신함
      onClose(); // 모달 닫기
    } catch (error) {
      console.error('보드 생성 중 오류 발생:', error);
    }
  };

  return (
    <div className="modal">
      <div className="modal-content">
        <h3>새 보드 추가</h3>
        <input
          type="text"
          placeholder="보드 이름"
          value={boardName}
          onChange={(e) => setBoardName(e.target.value)}
        />
        <button onClick={handleCreateBoard}>보드 생성</button>
        <button onClick={onClose}>취소</button>
      </div>
    </div>
  );
};

export default BoardModal;