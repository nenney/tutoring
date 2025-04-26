package com.assignment.tutoring.domain.availability.controller;

import com.assignment.tutoring.domain.availability.dto.AvailabilityRequestDto;
import com.assignment.tutoring.domain.availability.dto.AvailabilityResponseDto;
import com.assignment.tutoring.domain.availability.service.AvailabilityService;
import com.assignment.tutoring.domain.user.dto.TutorSimpleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/availabilities")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    // (튜터) 수업 가능한 시간대 등록 API
    @PostMapping("/tutors")
    public ResponseEntity<AvailabilityResponseDto> createAvailability(@RequestBody AvailabilityRequestDto request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(availabilityService.createAvailability(authentication.getName(), request));
    }

    @DeleteMapping("/{availabilityId}/tutors")
    public ResponseEntity<Void> deleteAvailability(
            @PathVariable Long availabilityId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        availabilityService.deleteAvailability(authentication.getName(), availabilityId);
        return ResponseEntity.noContent().build();
    }

    // (학생) 수업 가능한 시간대 조회 API
    @GetMapping("/search")
    public ResponseEntity<List<AvailabilityResponseDto>> searchAvailableTimeSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam int durationMinutes) {
        return ResponseEntity.ok(availabilityService.getAvailableTimeSlots(startDate, endDate, durationMinutes));
    }

    // (학생) 수업 가능한 튜터 조회 API
    @GetMapping("/tutors/search")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<TutorSimpleResponseDto>> searchAvailableTutors(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam int durationMinutes) {
        return ResponseEntity.ok(availabilityService.getAvailableTutors(startTime, endTime, durationMinutes));
    }

    @GetMapping("/tutors")
    public ResponseEntity<List<AvailabilityResponseDto>> getTutorAvailabilities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(availabilityService.getTutorAvailabilities(authentication.getName()));
    }
}
