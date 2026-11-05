package com.pshs.be.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// 오류 응답의 상태코드와 메시지 모음. 응답의 error_code에는 이름(INVALID_STATUS 등)이 그대로 들어감
@Getter
@RequiredArgsConstructor
public enum ErrorStatus {

    // 공통
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "요청 형식이 올바르지 않습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."),

    // 신고
    INVALID_STATUS(HttpStatus.BAD_REQUEST, "상태 값이 올바르지 않습니다."),
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 신고입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
