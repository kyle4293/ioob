package com.ioob.backend.domain.kanban.service;

import com.ioob.backend.domain.kanban.dto.BoardOrderDto;
import com.ioob.backend.domain.kanban.dto.BoardRequestDto;
import com.ioob.backend.domain.kanban.dto.BoardResponseDto;
import com.ioob.backend.domain.kanban.entity.Board;
import com.ioob.backend.domain.kanban.entity.Project;
import com.ioob.backend.domain.kanban.repository.BoardRepository;
import com.ioob.backend.domain.kanban.repository.ProjectRepository;
import com.ioob.backend.global.exception.CustomException;
import com.ioob.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public BoardResponseDto createBoard(BoardRequestDto boardRequestDto) {
        Project project = findProjectById(boardRequestDto.getProjectId());

        Board board = Board.builder()
                .name(boardRequestDto.getName())
                .project(project)
                .boardOrder(project.getBoards().size()+1)
                .build();

        return BoardResponseDto.of(boardRepository.save(board));
    }

    @Transactional(readOnly = true)
    public List<BoardResponseDto> getAllBoards(Long projectId) {
        return boardRepository.findByProjectId(projectId).stream()
                .map(BoardResponseDto::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public BoardResponseDto updateBoard(Long boardId, BoardRequestDto boardRequestDto) {
        Board board = findBoardById(boardId);
        board.setName(boardRequestDto.getName());
        return BoardResponseDto.of(board);
    }

    @Transactional
    public void updateBoardOrder(List<BoardOrderDto> boardOrders) {
        boardOrders.forEach(orderDto -> {
            Board board = findBoardById(orderDto.getBoardId());
            board.setBoardOrder(orderDto.getNewOrder());
        });

        boardRepository.flush();
    }

    @Transactional
    public void deleteBoard(Long boardId) {
        Board board = findBoardById(boardId);
        boardRepository.delete(board);
    }

    private Board findBoardById(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
    }

    private Project findProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));
    }
}
