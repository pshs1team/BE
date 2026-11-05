package com.pshs.be.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException e) {
        return toResponse(e.getErrorStatus());
    }

    // 바디가 비었거나 JSON 형식이 틀림, 경로변수 타입이 틀림 (예: /reports/abc)
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception e) {
        return toResponse(ErrorStatus.INVALID_REQUEST);
    }

    // 없는 주소(404), 잘못된 메서드(405) 같은 Spring 기본 오류는 원래 상태코드를 유지
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        if (e instanceof ErrorResponse errorResponse) {
            int status = errorResponse.getStatusCode().value();
            return ResponseEntity.status(status)
                    .body(ApiResponse.fail(status, "REQUEST_ERROR", errorResponse.getBody().getDetail()));
        }
        log.error("처리하지 못한 오류", e);
        return toResponse(ErrorStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiResponse<Void>> toResponse(ErrorStatus status) {
        return ResponseEntity.status(status.getHttpStatus()).body(ApiResponse.fail(status));
    }
}
