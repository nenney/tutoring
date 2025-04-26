package com.assignment.tutoring.domain.availability.repository;

import com.assignment.tutoring.domain.availability.entity.AvailabilitySlot;
import com.assignment.tutoring.domain.user.entity.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long> {
    @Query("SELECT s FROM AvailabilitySlot s WHERE s.isAvailable = true " +
           "AND s.startTime >= :startTime AND s.endTime <= :endTime")
    List<AvailabilitySlot> findByStartTimeBetweenAndIsAvailableTrue(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query("SELECT s FROM AvailabilitySlot s WHERE s.isAvailable = true " +
           "AND s.startTime >= :startTime AND s.startTime < :endTime")
    List<AvailabilitySlot> findByStartTimeAndEndTimeAndIsAvailableTrue(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    List<AvailabilitySlot> findByAvailabilityId(Long availabilityId);

    Optional<AvailabilitySlot> findByAvailabilityTutorAndStartTime(Tutor tutor, LocalDateTime startTime);

    @Query("SELECT s FROM AvailabilitySlot s WHERE s.availability.tutor = :tutor " +
           "AND s.startTime >= :startTime AND s.endTime <= :endTime")
    List<AvailabilitySlot> findByTutorAndTimeRange(
            @Param("tutor") Tutor tutor,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
} 