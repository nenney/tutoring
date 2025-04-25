package com.assignment.tutoring.domain.availability.service;

import com.assignment.tutoring.domain.availability.dto.AvailabilityRequestDto;
import com.assignment.tutoring.domain.availability.dto.AvailabilityResponseDto;
import com.assignment.tutoring.domain.availability.entity.Availability;
import com.assignment.tutoring.domain.availability.repository.AvailabilityRepository;
import com.assignment.tutoring.domain.user.dto.UserResponseDto;
import com.assignment.tutoring.domain.user.entity.Tutor;
import com.assignment.tutoring.domain.user.repository.TutorRepository;
import com.assignment.tutoring.global.error.AvailabilityException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilityService {
    private final AvailabilityRepository availabilityRepository;
    private final TutorRepository tutorRepository;

    // 튜터의 가능한 시간대 등록
    @Transactional
    public AvailabilityResponseDto createAvailability(Long tutorId, AvailabilityRequestDto request) {
        // 시간 유효성 검사
        request.validateTime();

        Tutor tutor = tutorRepository.findById(tutorId)
                .orElseThrow(AvailabilityException::tutorNotFound);

        // 시간대 중복 체크
        if (availabilityRepository.existsByTutorIdAndStartTimeAndEndTime(
                tutorId, request.getStartTime(), request.getEndTime())) {
            throw AvailabilityException.timeSlotAlreadyExists();
        }

        // 최소 수업 시간 검사 (30분)
        if (request.getEndTime().isBefore(request.getStartTime().plusMinutes(30))) {
            throw AvailabilityException.timeInvalid();
        }

        Availability availability = Availability.create(
                tutor,
                request.getStartTime(),
                request.getEndTime()
        );

        return new AvailabilityResponseDto(availabilityRepository.save(availability));
    }

    // 튜터의 가능한 시간대 삭제
    @Transactional
    public void deleteAvailability(Long tutorId, Long availabilityId) {
        Availability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(AvailabilityException::availabilityNotFound);

        if (!availability.getTutor().getId().equals(tutorId)) {
            throw AvailabilityException.notTutorAvailability();
        }

        availabilityRepository.delete(availability);
    }

    // 기간 & 수업 길이로 가능한 시간대 조회 (튜터 정보 없이)
    @Transactional(readOnly = true)
    public List<AvailabilityResponseDto> getAvailableTimeSlots(
            LocalDateTime startDate,
            LocalDateTime endDate,
            int durationMinutes
    ) {
        // 기간 유효성 검사
        if (startDate.isAfter(endDate)) {
            throw AvailabilityException.timeInvalid();
        }

        List<Availability> availabilities = availabilityRepository
                .findByStartTimeBetweenAndAvailableTrue(startDate, endDate);

        return availabilities.stream()
                .filter(availability ->
                        // 수업 길이에 맞는 시간대만 필터링
                        availability.getEndTime()
                                .isBefore(availability.getStartTime().plusMinutes(durationMinutes))
                )
                .map(availability -> new AvailabilityResponseDto(availability, true)) // 튜터 정보 제외
                .collect(Collectors.toList());
    }

    // 선택된 시간대 & 수업 길이로 가능한 튜터 조회
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAvailableTutors(
            LocalDateTime startTime,
            LocalDateTime endTime,
            int durationMinutes
    ) {
        // 시간 유효성 검사
        if (startTime.isAfter(endTime)) {
            throw AvailabilityException.timeInvalid();
        }

        List<Availability> availabilities = availabilityRepository
                .findByStartTimeAndEndTimeAndAvailableTrue(startTime, endTime);

        return availabilities.stream()
                .map(Availability::getTutor)
                .distinct() // 중복 튜터 제거
                .map(UserResponseDto::new)
                .collect(Collectors.toList());
    }

    // 특정 튜터의 가능한 시간대 조회 (튜터 정보 포함)
    @Transactional(readOnly = true)
    public List<AvailabilityResponseDto> getTutorAvailabilities(
            Long tutorId,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        List<Availability> availabilities = availabilityRepository
                .findByTutorIdAndStartTimeBetweenAndAvailableTrue(
                        tutorId, startTime, endTime);

        return availabilities.stream()
                .map(AvailabilityResponseDto::new) // 튜터 정보 포함
                .collect(Collectors.toList());
    }
}