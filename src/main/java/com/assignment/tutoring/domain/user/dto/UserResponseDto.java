package com.assignment.tutoring.domain.user.dto;

import com.assignment.tutoring.domain.user.entity.User;
import com.assignment.tutoring.global.common.TimeStamped;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserResponseDto extends TimeStamped {
    private Long id;
    private String userId;
    private String name;

    public UserResponseDto(User user) {
        super();
        this.id = user.getId();
        this.userId = user.getUserId();
        this.name = user.getName();
    }

    public static UserResponseDto from(User user) {
        return new UserResponseDto(user);
    }
}