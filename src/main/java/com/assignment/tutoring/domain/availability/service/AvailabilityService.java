package com.assignment.tutoring.domain.availability.service;

import com.assignment.tutoring.domain.availability.dto.AvailabilityRequestDto;
import com.assignment.tutoring.domain.availability.dto.AvailabilityResponseDto;
import com.assignment.tutoring.domain.availability.dto.AvailabilitySlotResponseDto;
import com.assignment.tutoring.domain.availability.entity.Availability;
import com.assignment.tutoring.domain.availability.entity.AvailabilitySlot;
import com.assignment.tutoring.domain.availability.repository.AvailabilityRepository;
import com.assignment.tutoring.domain.availability.repository.AvailabilitySlotRepository;
import com.assignment.tutoring.domain.lesson.entity.Lesson;
import com.assignment.tutoring.domain.user.dto.UserResponseDto;
import com.assignment.tutoring.domain.user.dto.TutorSimpleResponseDto;
import com.assignment.tutoring.domain.user.entity.Tutor;
import com.assignment.tutoring.domain.user.repository.TutorRepository;
import com.assignment.tutoring.global.error.ErrorCode;
import com.assignment.tutoring.global.error.AvailabilityException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.assignment.tutoring.domain.lesson.service.LessonService;
import com.assignment.tutoring.domain.availability.dto.AvailabilityDeleteRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AvailabilityService {
    private final AvailabilityRepository availabilityRepository;
    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final TutorRepository tutorRepository;
    private final LessonService lessonService;
    private static final Logger log = LoggerFactory.getLogger(AvailabilityService.class);

    // 튜터의 가능한 시간대 등록
    @Transactional
    public AvailabilityResponseDto createAvailability(String userId, AvailabilityRequestDto request) {
        Tutor tutor = tutorRepository.findByUserId(userId)
                .orElseThrow(() -> new AvailabilityException(ErrorCode.TUTOR_NOT_FOUND));

        // 시간 유효성 검사
        request.validateTime();

        // 기존 가용성과 중복 체크
        List<Availability> existingAvailabilities = availabilityRepository
                .findTutorAvailabilitiesInRange(tutor, request.getStartTime(), request.getEndTime());
        
        if (!existingAvailabilities.isEmpty()) {
            throw AvailabilityException.timeSlotAlreadyExists();
        }

        // 가용성 생성
        Availability availability = Availability.create(tutor, request.getStartTime(), request.getEndTime());
        
        // 저장
        Availability savedAvailability = availabilityRepository.save(availability);

        return new AvailabilityResponseDto(savedAvailability);
    }

    // 튜터의 가능한 시간대 삭제
    @Transactional
    public void deleteAvailability(AvailabilityDeleteRequestDto request) {
        log.info("Starting delete availability process for tutor");
        validateTime(request.getStartTime(), request.getEndTime());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Tutor tutor = tutorRepository.findByUserId(authentication.getName())
                .orElseThrow(() -> new AvailabilityException(ErrorCode.TUTOR_NOT_FOUND));
        log.info("Found tutor with ID: {}", tutor.getId());

        List<Availability> overlappingAvailabilities = availabilityRepository.findTutorAvailabilitiesInRange(
                tutor, request.getStartTime(), request.getEndTime());
        log.info("Found {} overlapping availabilities", overlappingAvailabilities.size());

        for (Availability availability : overlappingAvailabilities) {
            log.info("Processing availability: {}", availability.getId());
            
            // 기존 Availability의 시작/종료 시간
            LocalDateTime originalStartTime = availability.getStartTime();
            LocalDateTime originalEndTime = availability.getEndTime();
            
            // 삭제할 시간 범위
            LocalDateTime deleteStartTime = request.getStartTime();
            LocalDateTime deleteEndTime = request.getEndTime();
            
            // 삭제할 Slot들 찾기
            List<AvailabilitySlot> slotsToDelete = availabilitySlotRepository.findByAvailabilityAndTimeRange(
                availability, deleteStartTime, deleteEndTime);
            log.info("Found {} slots to delete", slotsToDelete.size());
            
            // 삭제할 Slot들에 연결된 수업 취소
            for (AvailabilitySlot slot : slotsToDelete) {
                if (slot.getLesson() != null) {
                    lessonService.cancelLessonsBySlot(slot);
                }
            }
            
            // 기존 Availability의 모든 Slot 삭제
            availabilitySlotRepository.deleteAll(availability.getSlots());
            log.info("Deleted all slots for availability: {}", availability.getId());
            
            // 기존 Availability 삭제
            availabilityRepository.delete(availability);
            log.info("Deleted availability: {}", availability.getId());
            
            // 새로운 Availability들을 생성
            if (originalStartTime.isBefore(deleteStartTime)) {
                // 이전 시간대의 새로운 Availability 생성 및 저장
                Availability beforeAvailability = Availability.create(
                    tutor,
                    originalStartTime,
                    deleteStartTime
                );
                beforeAvailability = availabilityRepository.save(beforeAvailability);
                log.info("Created new availability for before time range: {}", beforeAvailability.getId());
            }
            
            if (originalEndTime.isAfter(deleteEndTime)) {
                // 이후 시간대의 새로운 Availability 생성 및 저장
                Availability afterAvailability = Availability.create(
                    tutor,
                    deleteEndTime,
                    originalEndTime
                );
                afterAvailability = availabilityRepository.save(afterAvailability);
                log.info("Created new availability for after time range: {}", afterAvailability.getId());
            }
        }
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
                .findByStartTimeBetweenAndIsAvailableTrue(startDate, endDate);

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
    public List<TutorSimpleResponseDto> getAvailableTutors(
            LocalDateTime startTime,
            LocalDateTime endTime,
            int durationMinutes
    ) {
        // 시간 유효성 검사
        if (startTime.isAfter(endTime)) {
            throw AvailabilityException.timeInvalid();
        }

        List<AvailabilitySlot> slots = availabilitySlotRepository
                .findByStartTimeAndEndTimeAndIsAvailableTrue(startTime, endTime);

        return slots.stream()
                .filter(slot -> {
                    // 슬롯의 시작 시간부터 durationMinutes 동안 사용 가능한지 확인
                    LocalDateTime slotEndTime = slot.getStartTime().plusMinutes(durationMinutes);
                    return !slotEndTime.isAfter(slot.getEndTime()) && !slotEndTime.isAfter(endTime);
                })
                .map(slot -> slot.getAvailability().getTutor())
                .distinct()
                .map(TutorSimpleResponseDto::new)
                .collect(Collectors.toList());
    }

    // 특정 튜터의 가능한 시간대 조회 (튜터 정보 포함)
    @Transactional(readOnly = true)
    public List<AvailabilityResponseDto> getTutorAvailabilities(String userId) {
        Tutor tutor = tutorRepository.findByUserId(userId)
                .orElseThrow(() -> new AvailabilityException(ErrorCode.TUTOR_NOT_FOUND));

        List<Availability> availabilities = availabilityRepository
                .findTutorAvailabilitiesInRange(tutor, LocalDateTime.now(), LocalDateTime.now().plusHours(24));

        return availabilities.stream()
                .map(AvailabilityResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void bookSlot(Long slotId) {
        AvailabilitySlot slot = availabilitySlotRepository.findById(slotId)
                .orElseThrow(AvailabilityException::availabilityNotFound);
        
        validateSlotForBooking(slot);
        
        bookSlot(slot, null);
    }

    @Transactional
    public void cancelSlot(Long slotId) {
        AvailabilitySlot slot = availabilitySlotRepository.findById(slotId)
                .orElseThrow(AvailabilityException::availabilityNotFound);
        
        slot.cancel();
    }

    private void validateSlotForBooking(AvailabilitySlot slot) {
        if (!slot.isAvailable()) {
            throw new AvailabilityException(ErrorCode.SLOT_ALREADY_BOOKED);
        }
    }

    private void bookSlot(AvailabilitySlot slot, Lesson lesson) {
        AvailabilitySlot newSlot = AvailabilitySlot.createWithLesson(
            slot.getAvailability(),
            slot.getStartTime(),
            slot.getEndTime(),
            lesson
        );
        availabilitySlotRepository.save(newSlot);
    }

    private void validateTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new AvailabilityException(ErrorCode.AVAILABILITY_TIME_INVALID, "시작 시간은 종료 시간보다 이전이어야 합니다.");
        }
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new AvailabilityException(ErrorCode.AVAILABILITY_TIME_INVALID, "과거 시간은 삭제할 수 없습니다.");
        }
    }
}