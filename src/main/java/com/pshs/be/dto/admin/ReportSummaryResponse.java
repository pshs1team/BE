package com.pshs.be.dto.admin;

// 응답 키를 API 명세서와 같은 한글(전체/처리대기/처리중/처리완료)로 맞추려고 한글 이름 사용
public record ReportSummaryResponse(long 전체, long 처리대기, long 처리중, long 처리완료) {
}
