package com.assignment.tutoring.domain.user.dto;

import com.assignment.tutoring.domain.user.entity.User;
import lombok.Getter;

@Getter
public class LoginResponseDto {
    private Long id;
    private String userId;
    private String name;

    public LoginResponseDto(User user) {
        this.id = user.getId();
        this.userId = user.getUserId();
        this.name = user.getName();
    }

    public static LoginResponseDto from(User user) {
        return new LoginResponseDto(user);
    }
} 