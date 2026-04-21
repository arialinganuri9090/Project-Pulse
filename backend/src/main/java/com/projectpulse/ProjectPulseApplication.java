package com.projectpulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ProjectPulseApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProjectPulseApplication.class, args);
    }
}
