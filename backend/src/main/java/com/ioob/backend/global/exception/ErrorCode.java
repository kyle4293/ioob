package com.ioob.backend.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "Email is already registered"),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Email sending failed"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "Token not found"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid token"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Token expired"),
    AUTHORIZATION_REQUIRED(HttpStatus.UNAUTHORIZED, "Authorization is required"),
    DELETE_USER_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete user"),
    FETCH_USERS_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch users"),
    PERMISSION_DENIED(HttpStatus.FORBIDDEN, "You do not have permission to access this project"),
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "Project not found"),
    TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "Task not found"),
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "Board not found"),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Comment not found"),
    USER_NOT_IN_PROJECT(HttpStatus.BAD_REQUEST, "The user is not a member of the project");

    private final HttpStatus status;
    private final String message;
}
