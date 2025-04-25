package com.assignment.tutoring.domain.user.controller;

import com.assignment.tutoring.domain.user.dto.UserRequestDto;
import com.assignment.tutoring.domain.user.dto.UserResponseDto;
import com.assignment.tutoring.domain.user.entity.Tutor;
import com.assignment.tutoring.domain.user.entity.User;
import com.assignment.tutoring.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<UserResponseDto> login(@Valid @RequestBody UserRequestDto request) {
        User user = userService.login(request);
        return ResponseEntity.ok(new UserResponseDto(user));
    }
}
