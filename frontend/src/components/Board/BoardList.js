import React, { useState, useEffect, useCallback } from 'react';
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import BoardColumn from './BoardColumn';
import { boardService } from '../../services/BoardService';

const BoardList = ({ projectId, onBoardUpdateRef }) => {
  const [boards, setBoards] = useState([]);

  const fetchBoards = useCallback(async () => {
    try {
      const boardData = await boardService.getBoards(projectId);
      boardData.sort((a, b) => a.boardOrder - b.boardOrder);
      setBoards(boardData);
    } catch (error) {
      console.error('보드 정보를 불러오는 중 오류:', error);
    }
  }, [projectId]);

  const onBoardUpdate = useCallback(async () => {
    await fetchBoards();
  }, [fetchBoards]);

  useEffect(() => {
    fetchBoards();
    if (onBoardUpdateRef) {
      onBoardUpdateRef.current = onBoardUpdate;
    }
  }, [projectId, fetchBoards, onBoardUpdate, onBoardUpdateRef]);

  const moveBoard = (fromOrder, toOrder) => {
    const updatedBoards = [...boards];
    const movedBoard = updatedBoards.find((board) => board.boardOrder === fromOrder);
    if (movedBoard) {
      updatedBoards.splice(updatedBoards.indexOf(movedBoard), 1);
      updatedBoards.splice(toOrder - 1, 0, movedBoard);
      updatedBoards.forEach((board, index) => (board.boardOrder = index + 1));
      setBoards(updatedBoards);
    }
  };

  const saveBoardOrder = async () => {
    try {
      await boardService.updateBoardOrder(
        projectId,
        boards.map((board, index) => ({
          boardId: board.id,
          newOrder: index + 1,
        }))
      );
    } catch (error) {
      console.error('보드 순서 업데이트 중 오류 발생:', error);
    }
  };

  return (
    <DndProvider backend={HTML5Backend}>
      <div className="kanban-container">
        {boards.map((board) => (
          <BoardColumn
            key={board.id}
            boardOrder={board.boardOrder}
            board={board}
            projectId={projectId}
            moveBoard={moveBoard}
            saveBoardOrder={saveBoardOrder}
            onBoardUpdate={onBoardUpdate}
          />
        ))}
      </div>
    </DndProvider>
  );
};

export default BoardList;
