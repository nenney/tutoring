package com.assignment.tutoring.domain.lesson.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LessonTimeRequestDto {
    @NotNull(message = "튜터 ID는 필수입니다.")
    private final Long tutorId;

    @NotNull(message = "학생 ID는 필수입니다.")
    private final Long studentId;

    @NotNull(message = "시작 시간은 필수입니다.")
    private final LocalDateTime startTime;

    public LessonTimeRequestDto(Long tutorId, Long studentId, LocalDateTime startTime) {
        this.tutorId = tutorId;
        this.studentId = studentId;
        this.startTime = startTime;
    }
} 