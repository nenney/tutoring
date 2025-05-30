package com.assignment.tutoring.domain.availability.entity;

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
public class Availability extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id")
    private Tutor tutor;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @OneToMany(mappedBy = "availability", cascade = CascadeType.ALL)
    private List<AvailabilitySlot> slots = new ArrayList<>();

    public static Availability create(Tutor tutor, LocalDateTime startTime, LocalDateTime endTime) {
        Availability availability = new Availability();
        availability.tutor = tutor;
        availability.startTime = startTime;
        availability.endTime = endTime;
        availability.createTimeSlots();
        return availability;
    }

    public void createTimeSlots() {
        LocalDateTime currentTime = startTime;
        while (currentTime.isBefore(endTime)) {
            LocalDateTime nextTime = currentTime.plusMinutes(30);
            if (!nextTime.isAfter(endTime)) {
                AvailabilitySlot slot = AvailabilitySlot.create(this, currentTime, nextTime);
                this.slots.add(slot);
            }
            currentTime = nextTime;
        }
    }

    public boolean isOverlapping(LocalDateTime otherStartTime, LocalDateTime otherEndTime) {
        return !(this.endTime.isBefore(otherStartTime) || this.startTime.isAfter(otherEndTime));
    }
}