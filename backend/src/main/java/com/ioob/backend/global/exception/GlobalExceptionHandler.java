package com.ioob.backend.global.exception;

import com.ioob.backend.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<String>> handleCustomException(CustomException ex) {
        return ResponseEntity.status(ex.getErrorCode().getStatus())
                .body(ApiResponse.failure(ex.getErrorCode().getStatus().name(), ex.getMessage()));
    }

    // 기타 예외 처리 (기본 예외)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGeneralException(Exception ex) {
        return ResponseEntity.status(ErrorCode.GENERIC_ERROR.getStatus())
                .body(ApiResponse.failure(ErrorCode.GENERIC_ERROR.getMessage()));
    }

}
