package com.assignment.tutoring.domain.lesson.dto;

import com.assignment.tutoring.domain.lesson.entity.LessonType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class LessonRequestDto {
    private String tutorName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LessonType type; // THIRTY_MINUTES or SIXTY_MINUTES
} 