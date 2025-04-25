package com.assignment.tutoring.global.common;

import java.time.LocalDateTime;

public class CustomValidators {
    // 수업 시작 시간이 정각 또는 30분인지 검증
    public static boolean isValidLessonStartTime(LocalDateTime startTime) {
        return startTime.getMinute() == 0 || startTime.getMinute() == 30;
    }

    // 수업 길이가 30분 또는 60분인지 검증
    public static boolean isValidLessonDuration(int duration) {
        return duration == 30 || duration == 60;
    }

    // 수업 가능 시간이 과거가 아닌지 검증
    public static boolean isNotPastTime(LocalDateTime time) {
        return !time.isBefore(LocalDateTime.now());
    }

    // 수업 가능 시간이 30분 단위인지 검증
    public static boolean isHalfHourTime(LocalDateTime time) {
        return time.getMinute() % 30 == 0;
    }
}