package com.assignment.tutoring.domain.user.controller;

import com.assignment.tutoring.domain.user.dto.LoginResponseDto;
import com.assignment.tutoring.domain.user.dto.UserRequestDto;
import com.assignment.tutoring.domain.user.dto.UserResponseDto;
import com.assignment.tutoring.domain.user.entity.Tutor;
import com.assignment.tutoring.domain.user.entity.User;
import com.assignment.tutoring.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/tutors/signup")
    public ResponseEntity<UserResponseDto> tutorSignUp(@Valid @RequestBody UserRequestDto request) {
        Tutor tutor = userService.tutorSignUp(request);
        return ResponseEntity.ok(new UserResponseDto(tutor));
    }

    @PostMapping("/students/signup")
    public ResponseEntity<UserResponseDto> studentSignUp(@Valid @RequestBody UserRequestDto request) {
        User user = userService.studentSignUp(request);
        return ResponseEntity.ok(new UserResponseDto(user));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(Authentication authentication) {
        User user = userService.findByUserId(authentication.getName());
        return ResponseEntity.ok(LoginResponseDto.from(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }
}
