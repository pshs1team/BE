package com.pshs.be.controller;

import com.pshs.be.common.ApiResponse;
import com.pshs.be.common.SuccessStatus;
import com.pshs.be.dto.admin.AdminReportListResponse;
import com.pshs.be.dto.admin.ReportSummaryResponse;
import com.pshs.be.dto.admin.StatusChangeRequest;
import com.pshs.be.dto.admin.StatusChangeResponse;
import com.pshs.be.service.AdminReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// TODO: 로그인(JWT) 완성 후 관리자만 호출할 수 있게 권한 체크 추가 (401/403)
@Tag(name = "관리자 신고 관리", description = "관리자 대시보드와 신고상세(관리자) 화면에서 쓰는 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/reports")
public class AdminReportController {

    private final AdminReportService adminReportService;

    @Operation(summary = "신고 건수 통계", description = "전체/처리대기/처리중/처리완료 신고 건수를 조회합니다.")
    @GetMapping("/summary")
    public ApiResponse<ReportSummaryResponse> getSummary() {
        return ApiResponse.ok(SuccessStatus.REPORT_SUMMARY_OK, adminReportService.getSummary());
    }

    @Operation(summary = "전체 신고 목록", description = "모든 학생의 신고를 최신순으로 조회합니다. status를 보내면 해당 상태만 조회합니다.")
    @GetMapping
    public ApiResponse<AdminReportListResponse> getReports(
            @Parameter(description = "처리대기 / 처리중 / 처리완료 (없으면 전체)")
            @RequestParam(required = false) String status) {
        return ApiResponse.ok(SuccessStatus.REPORT_LIST_OK, adminReportService.getReports(status));
    }

    @Operation(summary = "신고 상태 변경", description = "신고 상태를 처리중 또는 처리완료로 변경합니다. 처리대기로는 되돌릴 수 없습니다.")
    @PatchMapping("/{reportId}/status")
    public ApiResponse<StatusChangeResponse> changeStatus(
            @Parameter(description = "상태를 바꿀 신고번호") @PathVariable Integer reportId,
            @RequestBody StatusChangeRequest request) {
        return ApiResponse.ok(SuccessStatus.REPORT_STATUS_CHANGED, adminReportService.changeStatus(reportId, request.status()));
    }
}
