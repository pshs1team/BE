package com.pshs.be.dto.admin;

import com.pshs.be.domain.Report;
import com.pshs.be.domain.Status;

import java.time.LocalDateTime;
import java.util.List;

public record AdminReportListResponse(List<Item> reports) {

    public record Item(
            Integer reportId,
            String location,
            String facility,
            Status status,
            String reporterName,
            LocalDateTime createdAt
    ) {
        public static Item from(Report report) {
            return new Item(
                    report.getReportId(),
                    report.getLocation(),
                    report.getFacility(),
                    report.getStatus(),
                    report.getUser().getName(),
                    report.getCreatedAt()
            );
        }
    }
}
