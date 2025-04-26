package com.assignment.tutoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TutoringApplication {

    public static void main(String[] args) {
        SpringApplication.run(TutoringApplication.class, args);
    }

}
