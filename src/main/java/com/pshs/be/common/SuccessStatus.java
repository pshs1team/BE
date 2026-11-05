package com.pshs.be.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// 성공 응답의 상태코드와 메시지 모음. 새 API를 만들면 여기에 추가
@Getter
@RequiredArgsConstructor
public enum SuccessStatus {

    // 관리자
    REPORT_SUMMARY_OK(HttpStatus.OK, "신고 건수를 조회했습니다."),
    REPORT_LIST_OK(HttpStatus.OK, "신고 목록을 조회했습니다."),
    REPORT_STATUS_CHANGED(HttpStatus.OK, "신고 상태가 변경되었습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
