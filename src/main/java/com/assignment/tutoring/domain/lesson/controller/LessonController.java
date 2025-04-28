package com.assignment.tutoring.domain.lesson.controller;

import com.assignment.tutoring.domain.lesson.dto.LessonRequestDto;
import com.assignment.tutoring.domain.lesson.dto.LessonResponseDto;
import com.assignment.tutoring.domain.lesson.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    // (학생) 수업 신청 API
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<LessonResponseDto> createLesson(@RequestBody LessonRequestDto request) {
        return ResponseEntity.ok(lessonService.createLesson(request));
    }

    // (튜터) 수업 취소 API
    @DeleteMapping("/{lessonId}")
    @PreAuthorize("hasRole('TUTOR')")
    public ResponseEntity<Void> cancelLesson(@PathVariable Long lessonId) {
        lessonService.cancelLesson(lessonId);
        return ResponseEntity.noContent().build();
    }

    // (학생) 자신이 신청한 수업 목록 조회 API
    @GetMapping("/students")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<LessonResponseDto>> getStudentLessons() {
        return ResponseEntity.ok(lessonService.getStudentLessons());
    }
} 