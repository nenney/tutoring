package com.assignment.tutoring.domain.availability.dto;

import com.assignment.tutoring.domain.availability.entity.Availability;
import com.assignment.tutoring.domain.user.dto.UserResponseDto;
import com.assignment.tutoring.global.common.TimeStamped;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AvailabilityResponseDto extends TimeStamped {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean available;
    private UserResponseDto tutor;  // Optional

    // 튜터 정보를 포함하는 생성자 (시간대 생성/삭제 API용)
    public AvailabilityResponseDto(Availability availability) {
        this.id = availability.getId();
        this.startTime = availability.getStartTime();
        this.endTime = availability.getEndTime();
        this.available = availability.isAvailable();
        this.tutor = new UserResponseDto(availability.getTutor());
    }

    // 튜터 정보를 포함하지 않는 생성자 (시간대 조회 API용)
    public AvailabilityResponseDto(Availability availability, boolean excludeTutor) {
        this.id = availability.getId();
        this.startTime = availability.getStartTime();
        this.endTime = availability.getEndTime();
        this.available = availability.isAvailable();
        if (!excludeTutor) {
            this.tutor = new UserResponseDto(availability.getTutor());
        }
    }
}