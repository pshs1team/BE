package com.pshs.be.service;

import com.pshs.be.common.ApiException;
import com.pshs.be.common.ErrorStatus;
import com.pshs.be.domain.Report;
import com.pshs.be.domain.Status;
import com.pshs.be.dto.admin.AdminReportListResponse;
import com.pshs.be.dto.admin.ReportSummaryResponse;
import com.pshs.be.dto.admin.StatusChangeResponse;
import com.pshs.be.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminReportService {

    private final ReportRepository reportRepository;

    public ReportSummaryResponse getSummary() {
        return new ReportSummaryResponse(
                reportRepository.count(),
                reportRepository.countByStatus(Status.처리대기),
                reportRepository.countByStatus(Status.처리중),
                reportRepository.countByStatus(Status.처리완료)
        );
    }

    public AdminReportListResponse getReports(String status) {
        List<Report> reports = (status == null)
                ? reportRepository.findAllByOrderByCreatedAtDesc()
                : reportRepository.findAllByStatusOrderByCreatedAtDesc(parseStatus(status));

        return new AdminReportListResponse(reports.stream().map(AdminReportListResponse.Item::from).toList());
    }

    @Transactional
    public StatusChangeResponse changeStatus(Integer reportId, String status) {
        Status newStatus = parseStatus(status);
        if (newStatus == Status.처리대기) {
            throw new ApiException(ErrorStatus.INVALID_STATUS); // 처리대기로는 되돌릴 수 없음
        }

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ApiException(ErrorStatus.REPORT_NOT_FOUND));
        report.changeStatus(newStatus);
        reportRepository.flush(); // 응답에 담을 updatedAt을 DB 반영 시점에 채우기 위해

        return StatusChangeResponse.from(report);
    }

    private Status parseStatus(String status) {
        try {
            return Status.valueOf(status);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ApiException(ErrorStatus.INVALID_STATUS);
        }
    }
}
