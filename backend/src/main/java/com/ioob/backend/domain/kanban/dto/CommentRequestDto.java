package com.ioob.backend.domain.kanban.dto;

import com.ioob.backend.domain.kanban.entity.Comment;
import lombok.Getter;

@Getter
public class CommentRequestDto {
    private String content;
}
