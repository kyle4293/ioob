package com.ioob.backend.exception;

import com.ioob.backend.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<String>> handleCustomException(CustomException ex) {
        return ResponseEntity.badRequest().body(ApiResponse.failure(ex.getErrorCode().getStatus().name(), ex.getMessage()));
    }

}
