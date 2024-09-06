package com.ioob.backend.service;

import com.ioob.backend.dto.BoardRequestDto;
import com.ioob.backend.dto.BoardResponseDto;

import java.util.List;

public interface BoardService {
    List<BoardResponseDto> getAllBoards(Long projectId);
    BoardResponseDto getBoardById(Long id);
    BoardResponseDto createBoard(BoardRequestDto boardRequestDto);
    BoardResponseDto updateBoard(Long id, BoardRequestDto boardRequestDto);
    void deleteBoard(Long id);
}
