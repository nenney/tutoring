package com.assignment.tutoring.domain.user.entity;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Student extends User {

    public Student(String userId, String password, String name) {
        super(userId, password, name);
    }
} 