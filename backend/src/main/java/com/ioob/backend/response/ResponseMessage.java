package com.ioob.backend.response;

import lombok.Getter;

@Getter
public enum ResponseMessage {
    USER_REGISTERED_SUCCESS("S001", "User registered successfully"),
    LOGIN_SUCCESS("S002", "Login successful"),
    TOKEN_REFRESH_SUCCESS("S003", "Token refreshed successfully"),
    EMAIL_VERIFIED_SUCCESS("S004", "Email verified successfully"),
    USER_DELETED_SUCCESS("S005", "User deleted successfully"),
    GET_PROFILE_SUCCESS("S006", "Get profile successfully"),

    // 추가된 프로젝트, 보드, 작업 성공 메시지
    PROJECT_CREATE_SUCCESS("P001", "Project created successfully"),
    PROJECT_UPDATE_SUCCESS("P002", "Project updated successfully"),
    PROJECT_DELETE_SUCCESS("P003", "Project deleted successfully"),
    PROJECT_FETCH_SUCCESS("P004", "Project fetched successfully"),

    BOARD_CREATE_SUCCESS("B001", "Board created successfully"),
    BOARD_UPDATE_SUCCESS("B002", "Board updated successfully"),
    BOARD_DELETE_SUCCESS("B003", "Board deleted successfully"),
    BOARD_FETCH_SUCCESS("B004", "Board fetched successfully"),

    TASK_CREATE_SUCCESS("T001", "Task created successfully"),
    TASK_UPDATE_SUCCESS("T002", "Task updated successfully"),
    TASK_DELETE_SUCCESS("T003", "Task deleted successfully"),
    TASK_FETCH_SUCCESS("T004", "Task fetched successfully");

    private final String code;
    private final String message;

    ResponseMessage(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
