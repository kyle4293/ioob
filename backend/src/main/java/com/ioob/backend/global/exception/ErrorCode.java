package com.ioob.backend.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    USER_REGISTER_FAILED(HttpStatus.BAD_REQUEST, "User registration failed"),
    LOGIN_FAILURE(HttpStatus.BAD_REQUEST, "Login failed"),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "Email is already registered"),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Email sending failed"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid token"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Token expired"),
    AUTHORIZATION_REQUIRED(HttpStatus.UNAUTHORIZED, "Authorization is required"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "Invalid password"),
    DELETE_USER_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete user"),
    FETCH_USERS_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch users"),
    EMAIL_VERIFICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "Email verification required"),
    PERMISSION_DENIED(HttpStatus.FORBIDDEN, "You do not have permission to access this project"),
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "Project not found"),
    TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "Task not found"),
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "Board not found"),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "Token not found");

    private final HttpStatus status;
    private final String message;
}
