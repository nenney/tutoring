package com.assignment.tutoring.domain.availability.repository;

import com.assignment.tutoring.domain.availability.entity.Availability;
import com.assignment.tutoring.domain.user.entity.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    // 튜터의 특정 시간대 중복 체크
    boolean existsByTutorIdAndStartTimeAndEndTime(
            Long tutorId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    // 특정 튜터의 가능한 시간대 조회
    List<Availability> findByTutorIdAndStartTimeBetween(
            Long tutorId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    // 특정 기간 내의 모든 가능한 시간대 조회
    List<Availability> findByStartTimeBetween(
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    // 특정 시간대의 가능한 시간대 조회
    List<Availability> findByStartTimeAndEndTime(
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    // 특정 튜터의 특정 기간 내 가능한 시간대 조회
    @Query("SELECT a FROM Availability a WHERE a.tutor = :tutor " +
            "AND ((a.startTime <= :endTime AND a.endTime >= :startTime))")
    List<Availability> findTutorAvailabilitiesInRange(
            @Param("tutor") Tutor tutor,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}