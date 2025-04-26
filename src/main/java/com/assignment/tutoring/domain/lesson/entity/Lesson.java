package com.assignment.tutoring.domain.lesson.entity;

import com.assignment.tutoring.domain.availability.entity.AvailabilitySlot;
import com.assignment.tutoring.domain.user.entity.Student;
import com.assignment.tutoring.domain.user.entity.Tutor;
import com.assignment.tutoring.global.common.TimeStamped;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lesson extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id")
    private Tutor tutor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    private List<AvailabilitySlot> slots = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LessonStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LessonType type;

    public static Lesson create(Tutor tutor, Student student, List<AvailabilitySlot> slots, LessonType type, LocalDateTime startTime, LocalDateTime endTime) {
        Lesson lesson = new Lesson();
        lesson.tutor = tutor;
        lesson.student = student;
        lesson.slots = slots;
        lesson.type = type;
        lesson.startTime = startTime;
        lesson.endTime = endTime;
        lesson.status = LessonStatus.SCHEDULED;
        return lesson;
    }

    public void cancel() {
        this.status = LessonStatus.CANCELLED;
        this.slots.forEach(slot -> slot.cancel());
    }

    public void complete() {
        this.status = LessonStatus.COMPLETED;
    }

    public void start() {
        this.status = LessonStatus.IN_PROGRESS;
    }
} 