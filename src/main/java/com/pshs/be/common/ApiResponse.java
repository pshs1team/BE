package com.pshs.be.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ApiResponse<T>(
        boolean success,
        int code,
        @JsonProperty("error_code") @JsonInclude(JsonInclude.Include.NON_NULL) String errorCode,
        String message,
        T data
) {

    public static <T> ApiResponse<T> ok(SuccessStatus status, T data) {
        return new ApiResponse<>(true, status.getHttpStatus().value(), null, status.getMessage(), data);
    }

    public static ApiResponse<Void> fail(ErrorStatus status) {
        return fail(status.getHttpStatus().value(), status.name(), status.getMessage());
    }

    public static ApiResponse<Void> fail(int code, String errorCode, String message) {
        return new ApiResponse<>(false, code, errorCode, message, null);
    }
}
