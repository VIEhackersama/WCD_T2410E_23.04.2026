package com.wcd.playerevaluation.exception;

import com.wcd.playerevaluation.dto.response.ApiResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Xử lý lỗi validation từ @Valid annotation (BindingResult)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.error("Dữ liệu không hợp lệ, vui lòng kiểm tra lại", fieldErrors));
    }

    /**
     * Xử lý lỗi business validation tùy chỉnh
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleValidationException(ValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.error(ex.getMessage(), null));
    }

    /**
     * Xử lý lỗi không tìm thấy tài nguyên
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDTO.error(ex.getMessage(), null));
    }

    /**
     * Xử lý lỗi JSON không hợp lệ
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.error("Body JSON không hợp lệ hoặc bị thiếu", null));
    }

    /**
     * Xử lý lỗi route không tồn tại
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleNoHandlerFound(NoHandlerFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDTO.error("Endpoint không tồn tại: " + ex.getRequestURL(), null));
    }

    /**
     * Xử lý các lỗi chung không dự đoán trước
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Lỗi hệ thống: " + ex.getMessage(), null));
    }
}
