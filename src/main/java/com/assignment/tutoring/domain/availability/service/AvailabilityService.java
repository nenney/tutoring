package com.assignment.tutoring.domain.availability.service;

import com.assignment.tutoring.domain.availability.dto.AvailabilityRequestDto;
import com.assignment.tutoring.domain.availability.dto.AvailabilityResponseDto;
import com.assignment.tutoring.domain.availability.dto.AvailabilitySlotResponseDto;
import com.assignment.tutoring.domain.availability.entity.Availability;
import com.assignment.tutoring.domain.availability.entity.AvailabilitySlot;
import com.assignment.tutoring.domain.availability.repository.AvailabilityRepository;
import com.assignment.tutoring.domain.availability.repository.AvailabilitySlotRepository;
import com.assignment.tutoring.domain.user.dto.UserResponseDto;
import com.assignment.tutoring.domain.user.dto.TutorSimpleResponseDto;
import com.assignment.tutoring.domain.user.entity.Tutor;
import com.assignment.tutoring.domain.user.repository.TutorRepository;
import com.assignment.tutoring.global.error.AvailabilityException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilityService {
    private final AvailabilityRepository availabilityRepository;
    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final TutorRepository tutorRepository;

    // 튜터의 가능한 시간대 등록
    @Transactional
    public AvailabilityResponseDto createAvailability(Long tutorId, AvailabilityRequestDto request) {
        // 시간 유효성 검사
        request.validateTime();

        Tutor tutor = tutorRepository.findById(tutorId)
                .orElseThrow(AvailabilityException::tutorNotFound);

        // 기존 가용성과 중복 체크
        List<Availability> existingAvailabilities = availabilityRepository
                .findByTutorIdAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                        tutorId, request.getStartTime(), request.getEndTime());
        
        if (!existingAvailabilities.isEmpty()) {
            throw AvailabilityException.timeSlotAlreadyExists();
        }

        // 가용성 생성
        Availability availability = Availability.create(tutor, request.getStartTime(), request.getEndTime());
        
        // 30분 단위 시간 슬롯 생성
        availability.createTimeSlots();
        
        // 저장
        Availability savedAvailability = availabilityRepository.save(availability);

        return new AvailabilityResponseDto(savedAvailability);
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
    public List<AvailabilityResponseDto> getAvailableTimeSlots(LocalDateTime startDate, LocalDateTime endDate, int durationMinutes) {
        // 기간 유효성 검사
        if (startDate.isAfter(endDate)) {
            throw AvailabilityException.timeInvalid();
        }

        // 30분 단위로 정확히 맞는지 검사
        if (startDate.getMinute() % 30 != 0 || endDate.getMinute() % 30 != 0) {
            throw AvailabilityException.timeInvalid();
        }

        List<AvailabilitySlot> slots = availabilitySlotRepository
                .findByStartTimeBetweenAndAvailableTrue(startDate, endDate);

        List<AvailabilityResponseDto> availableSlots = new ArrayList<>();

        for (AvailabilitySlot slot : slots) {
            LocalDateTime slotStartTime = slot.getStartTime();
            LocalDateTime slotEndTime = slotStartTime.plusMinutes(durationMinutes);

            // 슬롯의 종료 시간이 요청한 endDate를 초과하지 않는지 확인
            if (!slotEndTime.isAfter(endDate)) {
                // 30분 단위로 정확히 맞는지 확인
                if (slotStartTime.getMinute() % 30 == 0) {
                    availableSlots.add(createResponseDto(slot.getAvailability().getTutor(), slotStartTime, slotEndTime));
                }
            }
        }

        return availableSlots;
    }

    private AvailabilityResponseDto createResponseDto(Tutor tutor, LocalDateTime startTime, LocalDateTime endTime) {
        return AvailabilityResponseDto.builder()
                .startTime(startTime)
                .endTime(endTime)
                .tutor(new TutorSimpleResponseDto(tutor))
                .build();
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

        List<AvailabilitySlot> slots = availabilitySlotRepository
                .findByStartTimeAndEndTimeAndAvailableTrue(startTime, endTime);

        return slots.stream()
                .filter(slot -> {
                    LocalDateTime slotEndTime = slot.getStartTime().plusMinutes(durationMinutes);
                    return !slotEndTime.isAfter(slot.getEndTime());
                })
                .map(slot -> slot.getAvailability().getTutor())
                .distinct()
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
                .findByTutorIdAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                        tutorId, startTime, endTime);

        return availabilities.stream()
                .map(AvailabilityResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void bookSlot(Long slotId) {
        AvailabilitySlot slot = availabilitySlotRepository.findById(slotId)
                .orElseThrow(AvailabilityException::availabilityNotFound);
        
        if (!slot.isAvailable()) {
            throw AvailabilityException.timeSlotAlreadyExists();
        }
        
        slot.book();
    }

    @Transactional
    public void cancelSlot(Long slotId) {
        AvailabilitySlot slot = availabilitySlotRepository.findById(slotId)
                .orElseThrow(AvailabilityException::availabilityNotFound);
        
        slot.cancel();
    }
}