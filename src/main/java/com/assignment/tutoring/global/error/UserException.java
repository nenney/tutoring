package com.assignment.tutoring.global.error;

public class UserException extends BusinessException {
    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UserException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    // 정적 팩토리 메서드들
    public static UserException userNotFound() {
        return new UserException(ErrorCode.USER_NOT_FOUND);
    }

    public static UserException invalidUserRole() {
        return new UserException(ErrorCode.USER_ROLE_INVALID);
    }

    public static UserException userIdDuplicated() {
        return new UserException(ErrorCode.USER_ID_DUPLICATED);
    }

    public static UserException passwordMismatch() {
        return new UserException(ErrorCode.PASSWORD_MISMATCH);
    }
}