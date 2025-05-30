package com.assignment.tutoring.global.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    //공통 에러
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 에러입니다."),

    //유저 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    USER_ROLE_INVALID(HttpStatus.BAD_REQUEST, "잘못된 사용자 역할입니다."),
    USER_ID_DUPLICATED(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디입니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),

    // 수업 관련 에러
    LESSON_TIME_INVALID(HttpStatus.BAD_REQUEST, "수업 시간이 유효하지 않습니다."),
    LESSON_DURATION_INVALID(HttpStatus.BAD_REQUEST, "수업 길이가 유효하지 않습니다."),

    // 수업 가능 시간 관련 에러
    AVAILABILITY_NOT_FOUND(HttpStatus.NOT_FOUND, "수업 가능 시간을 찾을 수 없습니다."),
    NOT_TUTOR_AVAILABILITY(HttpStatus.FORBIDDEN, "해당 튜터의 수업 가능 시간이 아닙니다."),
    AVAILABILITY_TIME_INVALID(HttpStatus.BAD_REQUEST, "잘못된 수업 가능 시간입니다."),
    AVAILABILITY_TIME_OVERLAP(HttpStatus.BAD_REQUEST, "이미 등록된 수업 가능 시간입니다."),
    AVAILABILITY_PAST_TIME(HttpStatus.BAD_REQUEST, "과거 시간은 등록할 수 없습니다."),

    // User 관련 에러
    TUTOR_NOT_FOUND(HttpStatus.NOT_FOUND, "튜터를 찾을 수 없습니다."),
    STUDENT_NOT_FOUND(HttpStatus.NOT_FOUND, "학생을 찾을 수 없습니다."),

    // Availability 관련 에러
    AVAILABILITY_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 가용 시간입니다."),
    AVAILABILITY_OVERLAP(HttpStatus.BAD_REQUEST, "시간이 겹칩니다."),

    // AvailabilitySlot 관련 에러
    SLOT_NOT_FOUND(HttpStatus.NOT_FOUND, "예약 가능한 시간을 찾을 수 없습니다."),
    SLOT_ALREADY_BOOKED(HttpStatus.BAD_REQUEST, "이미 예약된 시간입니다."),
    SLOTS_NOT_CONSECUTIVE(HttpStatus.BAD_REQUEST, "연속된 슬롯이 아닙니다."),

    // Lesson 관련 에러
    LESSON_NOT_FOUND(HttpStatus.NOT_FOUND, "수업을 찾을 수 없습니다."),
    LESSON_ALREADY_CANCELLED(HttpStatus.BAD_REQUEST, "이미 취소된 수업입니다."),
    LESSON_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "이미 완료된 수업입니다."),

    // New error code
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");

    public final HttpStatus status;
    public final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
