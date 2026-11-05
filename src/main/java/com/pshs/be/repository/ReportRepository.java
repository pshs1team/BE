package com.pshs.be.repository;

import com.pshs.be.domain.Report;
import com.pshs.be.domain.Status;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Integer> {

    long countByStatus(Status status);

    // 목록에 신고자 이름이 필요해서 user를 같이 조회 (신고마다 쿼리가 따로 나가는 것 방지)
    @EntityGraph(attributePaths = "user")
    List<Report> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = "user")
    List<Report> findAllByStatusOrderByCreatedAtDesc(Status status);
}
