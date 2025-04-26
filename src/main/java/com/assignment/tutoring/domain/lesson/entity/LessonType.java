package com.assignment.tutoring.domain.lesson.entity;

import lombok.Getter;

@Getter
public enum LessonType {
    THIRTY_MINUTES(30, "30분 수업"),
    SIXTY_MINUTES(60, "60분 수업");

    private final int minutes;
    private final String description;

    LessonType(int minutes, String description) {
        this.minutes = minutes;
        this.description = description;
    }

    public int getDuration() {
        return minutes;
    }
} 