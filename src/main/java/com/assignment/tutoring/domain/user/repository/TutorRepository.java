package com.assignment.tutoring.domain.user.repository;

import com.assignment.tutoring.domain.user.entity.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TutorRepository extends JpaRepository<Tutor, Long> {
    Optional<Tutor> findByUserId(String userId);
}