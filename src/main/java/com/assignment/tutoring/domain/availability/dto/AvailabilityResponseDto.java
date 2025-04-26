package com.assignment.tutoring.domain.availability.dto;

import com.assignment.tutoring.domain.availability.entity.Availability;
import com.assignment.tutoring.domain.user.dto.TutorSimpleResponseDto;
import com.assignment.tutoring.global.common.TimeStamped;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AvailabilityResponseDto extends TimeStamped {
    @JsonIgnore
    @Override
    public LocalDateTime getCreatedAt() {
        return super.getCreatedAt();
    }

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private TutorSimpleResponseDto tutor;

    public AvailabilityResponseDto(Availability availability) {
        this.startTime = availability.getStartTime();
        this.endTime = availability.getEndTime();
        this.tutor = new TutorSimpleResponseDto(availability.getTutor());
    }

    public AvailabilityResponseDto(Availability availability, boolean excludeTutor) {
        this.startTime = availability.getStartTime();
        this.endTime = availability.getEndTime();
        if (!excludeTutor) {
            this.tutor = new TutorSimpleResponseDto(availability.getTutor());
        }
    }
}