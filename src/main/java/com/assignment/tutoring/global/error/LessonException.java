package com.assignment.tutoring.global.error;

import lombok.Getter;

@Getter
public class LessonException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String message;

    public LessonException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }

    public LessonException(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    // 정적 팩토리 메서드들
    public static LessonException timeInvalid() {
        return new LessonException(ErrorCode.LESSON_TIME_INVALID);
    }

    public static LessonException durationInvalid() {
        return new LessonException(ErrorCode.LESSON_DURATION_INVALID);
    }
}