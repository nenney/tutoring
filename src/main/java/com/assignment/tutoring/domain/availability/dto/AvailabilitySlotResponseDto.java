package com.assignment.tutoring.domain.availability.dto;

import com.assignment.tutoring.domain.availability.entity.AvailabilitySlot;
import com.assignment.tutoring.global.common.TimeStamped;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AvailabilitySlotResponseDto extends TimeStamped {
    private Long id;
    private Long availabilityId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean available;

    public AvailabilitySlotResponseDto(AvailabilitySlot slot) {
        this.id = slot.getId();
        this.availabilityId = slot.getAvailability().getId();
        this.startTime = slot.getStartTime();
        this.endTime = slot.getEndTime();
        this.available = slot.isAvailable();
    }
} 