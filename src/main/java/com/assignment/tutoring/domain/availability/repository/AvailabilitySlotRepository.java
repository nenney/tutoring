package com.assignment.tutoring.domain.availability.repository;

import com.assignment.tutoring.domain.availability.entity.AvailabilitySlot;
import com.assignment.tutoring.domain.user.entity.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long> {
    List<AvailabilitySlot> findByStartTimeBetweenAndAvailableTrue(
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    List<AvailabilitySlot> findByStartTimeAndEndTimeAndAvailableTrue(
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    List<AvailabilitySlot> findByAvailabilityId(Long availabilityId);

    Optional<AvailabilitySlot> findByAvailabilityTutorAndStartTime(Tutor tutor, LocalDateTime startTime);
} 