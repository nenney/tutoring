package com.assignment.tutoring.domain.availability.entity;

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
    @JoinColumn(name = "availability_id", nullable = false)
    private Availability availability;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean available;

    public static AvailabilitySlot create(Availability availability, LocalDateTime startTime, LocalDateTime endTime) {
        AvailabilitySlot slot = new AvailabilitySlot();
        slot.availability = availability;
        slot.startTime = startTime;
        slot.endTime = endTime;
        slot.available = true;
        return slot;
    }

    public void setAvailability(Availability availability) {
        this.availability = availability;
    }

    public void book() {
        this.available = false;
    }

    public void cancel() {
        this.available = true;
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean isOverlapping(LocalDateTime otherStartTime, LocalDateTime otherEndTime) {
        return !(this.endTime.isBefore(otherStartTime) || this.startTime.isAfter(otherEndTime));
    }
} 