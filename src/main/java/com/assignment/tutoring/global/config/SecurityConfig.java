package com.assignment.tutoring.global.config;

import com.assignment.tutoring.domain.user.entity.Tutor;
import com.assignment.tutoring.domain.user.entity.User;
import com.assignment.tutoring.domain.user.repository.StudentRepository;
import com.assignment.tutoring.domain.user.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TutorRepository tutorRepository;
    private final StudentRepository studentRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/tutors/signup", "/api/users/students/signup").permitAll()
                        .requestMatchers("/api/v1/availabilities/tutors/search").hasRole("STUDENT")
                        .requestMatchers("/api/v1/availabilities/search").hasRole("STUDENT")
                        .requestMatchers("/api/v1/availabilities/tutors").hasRole("TUTOR")
                        .requestMatchers("/api/v1/availabilities/tutors/{tutorId}").hasRole("TUTOR")
                        .requestMatchers("/api/v1/availabilities/{availabilityId}/tutors/{tutorId}").hasRole("TUTOR")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginProcessingUrl("/api/users/login")
                        .usernameParameter("userId")
                        .passwordParameter("password")
                        .successHandler((request, response, authentication) -> {
                            response.setStatus(200);
                        })
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(401);
                        })
                )
                .httpBasic(basic -> basic.init(http))
                .logout(logout -> logout
                        .logoutUrl("/api/users/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(200);
                        })
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = tutorRepository.findByUserId(username)
                    .map(tutor -> (User) tutor)
                    .orElseGet(() -> studentRepository.findByUserId(username)
                            .map(student -> (User) student)
                            .orElseThrow(() -> new UsernameNotFoundException("User not found")));

            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getUserId())
                    .password(user.getPassword())
                    .roles(user instanceof Tutor ? "TUTOR" : "STUDENT")
                    .build();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("JSESSIONID");
        serializer.setCookiePath("/");
        serializer.setDomainNamePattern("^.+?\\.(\\w+\\.[a-z]+)$");
        return serializer;
    }
} 