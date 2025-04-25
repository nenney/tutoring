package com.assignment.tutoring.domain.user.repository;

import com.assignment.tutoring.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface UserRepository<T extends User> extends JpaRepository<T, Long> {
    Optional<T> findByUserId(String userId);
}