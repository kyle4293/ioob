package com.ioob.backend.service.impl;

import com.ioob.backend.dto.BoardRequestDto;
import com.ioob.backend.dto.BoardResponseDto;
import com.ioob.backend.entity.Board;
import com.ioob.backend.entity.Project;
import com.ioob.backend.exception.CustomException;
import com.ioob.backend.exception.ErrorCode;
import com.ioob.backend.repository.BoardRepository;
import com.ioob.backend.repository.ProjectRepository;
import com.ioob.backend.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final ProjectRepository projectRepository;  // 프로젝트를 조회하기 위한 Repository 추가

    @Override
    @Transactional(readOnly = true)
    public List<BoardResponseDto> getAllBoards(Long projectId) {
        return boardRepository.findByProjectId(projectId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BoardResponseDto getBoardById(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
        return convertToDto(board);
    }

    @Override
    @Transactional
    public BoardResponseDto createBoard(BoardRequestDto boardRequestDto) {
        Project project = projectRepository.findById(boardRequestDto.getProjectId())
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));  // 프로젝트를 조회하여 설정

        Board board = Board.builder()
                .name(boardRequestDto.getName())
                .project(project)  // 프로젝트 객체 설정
                .build();
        board = boardRepository.save(board);
        return convertToDto(board);
    }

    @Override
    @Transactional
    public BoardResponseDto updateBoard(Long id, BoardRequestDto boardRequestDto) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        board.setName(boardRequestDto.getName());
        return convertToDto(board);
    }

    @Override
    @Transactional
    public void deleteBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
        boardRepository.delete(board);
    }

    private BoardResponseDto convertToDto(Board board) {
        return new BoardResponseDto(board.getId(), board.getName(), board.getProject().getId());
    }
}
