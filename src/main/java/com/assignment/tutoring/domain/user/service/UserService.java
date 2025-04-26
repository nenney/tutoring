package com.assignment.tutoring.domain.user.service;

import com.assignment.tutoring.domain.user.dto.LoginRequestDto;
import com.assignment.tutoring.domain.user.dto.UserRequestDto;
import com.assignment.tutoring.domain.user.entity.Student;
import com.assignment.tutoring.domain.user.entity.Tutor;
import com.assignment.tutoring.domain.user.entity.User;
import com.assignment.tutoring.domain.user.repository.StudentRepository;
import com.assignment.tutoring.domain.user.repository.TutorRepository;
import com.assignment.tutoring.global.error.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final TutorRepository tutorRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    // 튜터 회원가입
    @Transactional
    public Tutor tutorSignUp(UserRequestDto request) {
        // 아이디 중복 체크
        if (tutorRepository.findByUserId(request.getUserId()).isPresent() ||
                studentRepository.findByUserId(request.getUserId()).isPresent()) {
            throw UserException.userIdDuplicated();
        }

        Tutor tutor = new Tutor(request.getUserId(), request.getPassword(), request.getName());
        tutor.encodePassword(passwordEncoder);
        return tutorRepository.save(tutor);
    }

    // 학생 회원가입
    @Transactional
    public Student studentSignUp(UserRequestDto request) {
        // 아이디 중복 체크
        if (tutorRepository.findByUserId(request.getUserId()).isPresent() ||
                studentRepository.findByUserId(request.getUserId()).isPresent()) {
            throw UserException.userIdDuplicated();
        }

        Student student = new Student(request.getUserId(), request.getPassword(), request.getName());
        student.encodePassword(passwordEncoder);
        return studentRepository.save(student);
    }

    // 로그인
    public User login(LoginRequestDto request) {
        Optional<User> user = tutorRepository.findByUserId(request.getUserId())
                .map(tutor -> (User) tutor)
                .or(() -> studentRepository.findByUserId(request.getUserId())
                        .map(student -> (User) student));

        User foundUser = user.orElseThrow(UserException::userNotFound);

        if (!foundUser.matchPassword(passwordEncoder, request.getPassword())) {
            throw UserException.passwordMismatch();
        }

        return foundUser;
    }

    public User findByUserId(String userId) {
        Optional<Tutor> tutor = tutorRepository.findByUserId(userId);
        if (tutor.isPresent()) {
            return tutor.get();
        }

        Optional<Student> student = studentRepository.findByUserId(userId);
        if (student.isPresent()) {
            return student.get();
        }

        throw UserException.userNotFound();
    }
}