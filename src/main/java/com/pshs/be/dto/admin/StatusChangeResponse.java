package com.pshs.be.dto.admin;

import com.pshs.be.domain.Report;
import com.pshs.be.domain.Status;

import java.time.LocalDateTime;

public record StatusChangeResponse(Integer reportId, Status status, LocalDateTime updatedAt) {

    public static StatusChangeResponse from(Report report) {
        return new StatusChangeResponse(report.getReportId(), report.getStatus(), report.getUpdatedAt());
    }
}
