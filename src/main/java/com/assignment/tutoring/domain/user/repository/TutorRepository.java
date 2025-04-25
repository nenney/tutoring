package com.assignment.tutoring.domain.user.repository;

import com.assignment.tutoring.domain.user.entity.Tutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TutorRepository extends UserRepository<Tutor> {
} 