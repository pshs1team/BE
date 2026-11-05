package com.pshs.be.dto.admin;

// 잘못된 값도 INVALID_STATUS로 응답하려고 enum 대신 문자열로 받음
public record StatusChangeRequest(String status) {
}
