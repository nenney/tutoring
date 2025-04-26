package com.assignment.tutoring.domain.availability.entity;

import com.assignment.tutoring.domain.lesson.entity.Lesson;
import com.assignment.tutoring.domain.user.entity.Tutor;
import com.assignment.tutoring.global.common.TimeStamped;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AvailabilitySlot extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "availability_id")
    private Availability availability;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id")
    private Tutor tutor;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private boolean isAvailable = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    public static AvailabilitySlot create(Availability availability, LocalDateTime startTime, LocalDateTime endTime) {
        AvailabilitySlot slot = new AvailabilitySlot();
        slot.availability = availability;
        slot.startTime = startTime;
        slot.endTime = endTime;
        slot.isAvailable = true;
        return slot;
    }

    public static AvailabilitySlot createWithLesson(Availability availability, LocalDateTime startTime, LocalDateTime endTime, Lesson lesson) {
        AvailabilitySlot slot = new AvailabilitySlot();
        slot.availability = availability;
        slot.startTime = startTime;
        slot.endTime = endTime;
        slot.isAvailable = false;
        slot.lesson = lesson;
        return slot;
    }

    public void cancel() {
        this.lesson = null;
        this.isAvailable = true;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public boolean isOverlapping(LocalDateTime otherStartTime, LocalDateTime otherEndTime) {
        return !(this.endTime.isBefore(otherStartTime) || this.startTime.isAfter(otherEndTime));
    }
} 