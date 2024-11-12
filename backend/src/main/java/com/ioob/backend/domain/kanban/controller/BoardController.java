package com.ioob.backend.domain.kanban.controller;

import com.ioob.backend.domain.kanban.dto.BoardRequestDto;
import com.ioob.backend.domain.kanban.dto.BoardResponseDto;
import com.ioob.backend.domain.kanban.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Board Management", description = "보드(Board) 관련 CRUD API")
@RestController
@RequestMapping("/api/projects/{projectId}/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "보드 목록 조회", description = "프로젝트 내 모든 보드를 조회하는 API")
    @GetMapping
    public List<BoardResponseDto> getAllBoards(@PathVariable Long projectId) {
        return boardService.getAllBoards(projectId);
    }

    @Operation(summary = "보드 생성", description = "새로운 보드를 생성하는 API")
    @PostMapping
    public BoardResponseDto createBoard(@RequestBody BoardRequestDto boardRequestDto) {
        return boardService.createBoard(boardRequestDto);
    }

    @Operation(summary = "보드 수정", description = "ID를 통해 특정 보드를 수정하는 API")
    @PutMapping("/{boardId}")
    public BoardResponseDto updateBoard(@PathVariable Long boardId, @RequestBody BoardRequestDto boardRequestDto) {
        return boardService.updateBoard(boardId, boardRequestDto);
    }

    @Operation(summary = "보드 삭제", description = "ID를 통해 특정 보드를 삭제하는 API")
    @DeleteMapping("/{boardId}")
    public void deleteBoard(@PathVariable Long boardId) {
        boardService.deleteBoard(boardId);
    }
}
