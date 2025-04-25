package com.assignment.tutoring.global.error;

public class AvailabilityException extends BusinessException {
    public AvailabilityException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AvailabilityException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    // 정적 팩토리 메서드들
    public static AvailabilityException tutorNotFound() {
        return new AvailabilityException(ErrorCode.USER_NOT_FOUND);
    }

    public static AvailabilityException availabilityNotFound() {
        return new AvailabilityException(ErrorCode.AVAILABILITY_NOT_FOUND);
    }

    public static AvailabilityException notTutorAvailability() {
        return new AvailabilityException(ErrorCode.NOT_TUTOR_AVAILABILITY);
    }

    public static AvailabilityException timeInvalid() {
        return new AvailabilityException(ErrorCode.AVAILABILITY_TIME_INVALID);
    }

    public static AvailabilityException timeOverlap() {
        return new AvailabilityException(ErrorCode.AVAILABILITY_TIME_OVERLAP);
    }

    public static AvailabilityException pastTime() {
        return new AvailabilityException(ErrorCode.AVAILABILITY_PAST_TIME);
    }

    public static AvailabilityException timeSlotAlreadyExists() {
        return new AvailabilityException(ErrorCode.AVAILABILITY_TIME_OVERLAP);
    }
}