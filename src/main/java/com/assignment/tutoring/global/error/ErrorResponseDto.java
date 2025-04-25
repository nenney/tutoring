package com.assignment.tutoring.global.error;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponseDto {
    private HttpStatus status;
    private String message;

    private ErrorResponseDto(ErrorCode errorCode) {
        this.message = errorCode.message;
        this.status = errorCode.status;
    }

    public static ErrorResponseDto of(ErrorCode errorCode) {
        return new ErrorResponseDto(errorCode);
    }

    public static ErrorResponseDto of(ErrorCode errorCode, String message) {
        ErrorResponseDto response = new ErrorResponseDto(errorCode);
        response.message = message;
        return response;
    }
}
