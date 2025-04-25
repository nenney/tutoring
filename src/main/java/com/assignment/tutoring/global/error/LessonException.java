package com.assignment.tutoring.global.error;

public class LessonException extends BusinessException {
    public LessonException(ErrorCode errorCode) {
        super(errorCode);
    }

    public LessonException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    // 정적 팩토리 메서드들
    public static LessonException timeInvalid() {
        return new LessonException(ErrorCode.LESSON_TIME_INVALID);
    }

    public static LessonException durationInvalid() {
        return new LessonException(ErrorCode.LESSON_DURATION_INVALID);
    }
}