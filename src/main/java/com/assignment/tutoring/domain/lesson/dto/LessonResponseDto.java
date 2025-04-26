package com.assignment.tutoring.domain.lesson.dto;

import com.assignment.tutoring.domain.lesson.entity.Lesson;
import com.assignment.tutoring.domain.lesson.entity.LessonStatus;
import com.assignment.tutoring.domain.lesson.entity.LessonType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LessonResponseDto {
    private LocalDateTime createdAt;
    private TutorInfo tutor;
    private StudentInfo student;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LessonStatus status;
    private LessonType type;

    public static LessonResponseDto from(Lesson lesson) {
        LessonResponseDto dto = new LessonResponseDto();
        dto.createdAt = lesson.getCreatedAt();
        dto.tutor = new TutorInfo(lesson.getTutor().getName());
        dto.student = new StudentInfo(lesson.getStudent().getName());
        dto.startTime = lesson.getStartTime();
        dto.endTime = lesson.getEndTime();
        dto.status = lesson.getStatus();
        dto.type = lesson.getType();
        return dto;
    }

    @Getter
    private static class TutorInfo {
        private final String name;

        public TutorInfo(String name) {
            this.name = name;
        }
    }

    @Getter
    private static class StudentInfo {
        private final String name;

        public StudentInfo(String name) {
            this.name = name;
        }
    }
} 