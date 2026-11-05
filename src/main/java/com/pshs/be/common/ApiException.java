package com.pshs.be.common;

import lombok.Getter;

// 사용 예: throw new ApiException(ErrorStatus.REPORT_NOT_FOUND);
@Getter
public class ApiException extends RuntimeException {

    private final ErrorStatus errorStatus;

    public ApiException(ErrorStatus errorStatus) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
    }
}
