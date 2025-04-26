package com.assignment.tutoring.domain.lesson.service;

import com.assignment.tutoring.domain.availability.entity.AvailabilitySlot;
import com.assignment.tutoring.domain.availability.repository.AvailabilitySlotRepository;
import com.assignment.tutoring.domain.lesson.dto.LessonRequestDto;
import com.assignment.tutoring.domain.lesson.dto.LessonResponseDto;
import com.assignment.tutoring.domain.lesson.dto.LessonTimeRequestDto;
import com.assignment.tutoring.domain.lesson.entity.Lesson;
import com.assignment.tutoring.domain.lesson.entity.LessonType;
import com.assignment.tutoring.domain.lesson.repository.LessonRepository;
import com.assignment.tutoring.domain.user.entity.Student;
import com.assignment.tutoring.domain.user.entity.Tutor;
import com.assignment.tutoring.domain.user.repository.StudentRepository;
import com.assignment.tutoring.domain.user.repository.TutorRepository;
import com.assignment.tutoring.global.error.ErrorCode;
import com.assignment.tutoring.global.error.LessonException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LessonService {
    private final LessonRepository lessonRepository;
    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final TutorRepository tutorRepository;
    private final StudentRepository studentRepository;
    private static final Logger log = LoggerFactory.getLogger(LessonService.class);

    @Transactional
    public LessonResponseDto createLesson(LessonRequestDto request) {
        // 1. 튜터 조회
        Tutor tutor = tutorRepository.findByName(request.getTutorName())
                .orElseThrow(() -> new LessonException(ErrorCode.TUTOR_NOT_FOUND));

        // 2. 현재 로그인한 학생 정보 가져오기 (SecurityContext에서)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Student student = studentRepository.findByUserId(authentication.getName())
                .orElseThrow(() -> new LessonException(ErrorCode.STUDENT_NOT_FOUND));

        // 3. 수업 시간 유효성 검사
        validateLessonTime(request.getStartTime(), request.getEndTime(), request.getType());

        // 4. 해당 시간대의 availability 조회
        List<AvailabilitySlot> slots = availabilitySlotRepository.findByTutorAndTimeRange(
                tutor, request.getStartTime(), request.getEndTime());

        // 5. 슬롯이 예약 가능한지 확인
        validateSlotsForBooking(slots);

        // 6. 수업 생성 및 저장
        final Lesson lesson = Lesson.create(tutor, student, slots, request.getType(), request.getStartTime(), request.getEndTime());
        lessonRepository.save(lesson);

        // 7. 슬롯 상태 업데이트
        slots.forEach(slot -> {
            AvailabilitySlot newSlot = AvailabilitySlot.createWithLesson(
                slot.getAvailability(),
                slot.getStartTime(),
                slot.getEndTime(),
                lesson
            );
            availabilitySlotRepository.save(newSlot);
        });

        return LessonResponseDto.from(lesson);
    }

    private void validateLessonTime(LocalDateTime startTime, LocalDateTime endTime, LessonType type) {
        Duration duration = Duration.between(startTime, endTime);
        long minutes = duration.toMinutes();

        if (type == LessonType.THIRTY_MINUTES && minutes != 30) {
            throw new LessonException(ErrorCode.LESSON_DURATION_INVALID, "선택한 수업 길이가 30분 입니다.");
        }
        if (type == LessonType.SIXTY_MINUTES && minutes != 60) {
            throw new LessonException(ErrorCode.LESSON_DURATION_INVALID, "선택한 수업 길이가 60분 입니다.");
        }
    }

    private void validateSlotsForBooking(List<AvailabilitySlot> slots) {
        if (slots.isEmpty()) {
            throw new LessonException(ErrorCode.SLOT_NOT_FOUND);
        }

        if (slots.stream().anyMatch(slot -> !slot.isAvailable())) {
            throw new LessonException(ErrorCode.SLOT_ALREADY_BOOKED);
        }
    }

    @Transactional
    public void cancelLesson(Long lessonId) {
        log.info("Cancelling lesson with ID: {}", lessonId);
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new LessonException(ErrorCode.LESSON_NOT_FOUND));
        lesson.cancel();
        lessonRepository.save(lesson);
        log.info("Successfully cancelled lesson: {}", lesson);
    }

    @Transactional(readOnly = true)
    public LessonResponseDto getLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new LessonException(ErrorCode.LESSON_NOT_FOUND));
        return LessonResponseDto.from(lesson);
    }

    @Transactional(readOnly = true)
    public List<LessonResponseDto> getStudentLessons() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Student student = studentRepository.findByUserId(authentication.getName())
                .orElseThrow(() -> new LessonException(ErrorCode.STUDENT_NOT_FOUND));

        List<Lesson> lessons = lessonRepository.findByStudentId(student.getId());
        return lessons.stream()
                .map(LessonResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LessonResponseDto> getTutorLessons(Long tutorId) {
        List<Lesson> lessons = lessonRepository.findByTutorId(tutorId);
        return lessons.stream()
                .map(LessonResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelLessonsBySlot(AvailabilitySlot slot) {
        log.info("Cancelling lessons for slot: {}", slot.getId());
        if (slot.getLesson() != null) {
            cancelLesson(slot.getLesson().getId());
        }
    }

    @Transactional
    public void cancelLessons(Tutor tutor, LocalDateTime startTime) {
        List<Lesson> lessons = lessonRepository.findByTutorAndStartTime(tutor, startTime);
        for (Lesson lesson : lessons) {
            lesson.cancel();
        }
    }
} 