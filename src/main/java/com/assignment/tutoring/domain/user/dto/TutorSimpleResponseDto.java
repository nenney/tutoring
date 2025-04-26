package com.assignment.tutoring.domain.user.dto;

import com.assignment.tutoring.domain.user.entity.Tutor;
import lombok.Getter;

@Getter
public class TutorSimpleResponseDto {
    private final String name;

    public TutorSimpleResponseDto(Tutor tutor) {
        this.name = tutor.getName();
    }
} 