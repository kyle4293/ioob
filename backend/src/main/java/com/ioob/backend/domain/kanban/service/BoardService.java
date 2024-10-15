package com.ioob.backend.service;

import com.ioob.backend.dto.BoardRequestDto;
import com.ioob.backend.dto.BoardResponseDto;
import com.ioob.backend.entity.Board;
import com.ioob.backend.entity.Project;
import com.ioob.backend.entity.RoleName;
import com.ioob.backend.exception.CustomException;
import com.ioob.backend.exception.ErrorCode;
import com.ioob.backend.repository.BoardRepository;
import com.ioob.backend.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final ProjectRepository projectRepository;
    private final RoleService roleService;

    
    @Transactional(readOnly = true)
    public List<BoardResponseDto> getAllBoards(Long projectId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // 프로젝트 권한 확인
        if (!roleService.isUserInProject(projectId, email)) {
            throw new CustomException(ErrorCode.AUTHORIZATION_REQUIRED);
        }

        return boardRepository.findByProjectId(projectId).stream()
                .map(BoardResponseDto::new)
                .collect(Collectors.toList());
    }

   
    @Transactional(readOnly = true)
    public BoardResponseDto getBoardById(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        // 프로젝트 권한 확인
        if (!roleService.isUserInProject(board.getProject().getId(), email)) {
            throw new CustomException(ErrorCode.AUTHORIZATION_REQUIRED);
        }

        return new BoardResponseDto(board);
    }

   
    @Transactional
    public BoardResponseDto createBoard(BoardRequestDto boardRequestDto) {
        Project project = projectRepository.findById(boardRequestDto.getProjectId())
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // 프로젝트 내부에 있는지 확인
        if (!roleService.isUserInProject(project.getId(), email)) {
            throw new CustomException(ErrorCode.AUTHORIZATION_REQUIRED);
        }

        Board board = Board.builder()
                .name(boardRequestDto.getName())
                .project(project)
                .build();
        board = boardRepository.save(board);
        return new BoardResponseDto(board);
    }

   
    @Transactional
    public BoardResponseDto updateBoard(Long id, BoardRequestDto boardRequestDto) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Long projectId = board.getProject().getId();

        // 권한 확인 (프로젝트 관리자 확인)
        if (!roleService.hasPermission(projectId, RoleName.ROLE_PROJECT_ADMIN, email)) {
            throw new CustomException(ErrorCode.AUTHORIZATION_REQUIRED);
        }

        board.setName(boardRequestDto.getName());

        return new BoardResponseDto(board);
    }

   
    @Transactional
    public void deleteBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Long projectId = board.getProject().getId();

        // 관리자 권한 확인
        if (!roleService.hasPermission(projectId, RoleName.ROLE_PROJECT_ADMIN, email)) {
            throw new CustomException(ErrorCode.AUTHORIZATION_REQUIRED);
        }
        boardRepository.delete(board);
    }
}
