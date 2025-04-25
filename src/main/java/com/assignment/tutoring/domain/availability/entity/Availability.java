package com.assignment.tutoring.domain.availability.entity;

import com.assignment.tutoring.domain.user.entity.Tutor;
import com.assignment.tutoring.global.common.TimeStamped;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Availability extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Tutor tutor;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private boolean available;

    public static Availability create(Tutor tutor, LocalDateTime startTime, LocalDateTime endTime) {
        Availability availability = new Availability();
        availability.tutor = tutor;
        availability.startTime = startTime;
        availability.endTime = endTime;
        availability.available = true;
        return availability;
    }
}