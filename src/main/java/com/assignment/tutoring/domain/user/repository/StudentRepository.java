package com.assignment.tutoring.domain.user.repository;

import com.assignment.tutoring.domain.user.entity.Student;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends UserRepository<Student> {
} 