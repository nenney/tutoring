package com.assignment.tutoring.domain.lesson.repository;

import com.assignment.tutoring.domain.lesson.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByStudentId(Long studentId);
    List<Lesson> findByTutorId(Long tutorId);
} 