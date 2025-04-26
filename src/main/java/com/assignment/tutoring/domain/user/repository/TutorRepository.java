package com.assignment.tutoring.domain.user.repository;

import com.assignment.tutoring.domain.user.entity.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TutorRepository extends JpaRepository<Tutor, Long> {
    Optional<Tutor> findByUserId(String userId);
    Optional<Tutor> findByName(String name);
}