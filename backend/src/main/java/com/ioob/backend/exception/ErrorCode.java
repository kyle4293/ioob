package com.ioob.backend.exception;

public enum ErrorCode {
    USER_REGISTER_FAILED("E001", "User registration failed"),
    LOGIN_FAILURE("E002", "Login failed"),
    EMAIL_ALREADY_EXISTS("E003", "Email is already registered"),
    EMAIL_SEND_FAILED("E004", "Email sending failed"),
    USER_NOT_FOUND("E005", "User not found"),
    INVALID_TOKEN("E006", "Invalid token"),
    TOKEN_EXPIRED("E007", "Token expired"),
    AUTHORIZATION_REQUIRED("E008", "Authorization is required"),
    INVALID_PASSWORD("E009", "Invalid password"),
    DELETE_USER_FAILED("E010", "Failed to delete user"),
    FETCH_USERS_FAILED("E011", "Failed to fetch users"),
    EMAIL_VERIFICATION_REQUIRED("E012", "Email verification required"),
    PERMISSION_DENIED("E013", "You do not have permission to access this project"),
    PROJECT_NOT_FOUND("E014", "Project not found"),
    TASK_NOT_FOUND("E015", "Task not found"),
    BOARD_NOT_FOUND("E016", "Board not found");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
