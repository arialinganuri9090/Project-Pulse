package com.projectpulse.seeder;

import com.projectpulse.model.User;
import com.projectpulse.model.enums.Role;
import com.projectpulse.model.enums.UserStatus;
import com.projectpulse.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin-email}")
    private String adminEmail;

    @Value("${app.admin-password}")
    private String adminPassword;

    @Value("${app.admin-first-name}")
    private String adminFirstName;

    @Value("${app.admin-last-name}")
    private String adminLastName;

    @Autowired
    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setFirstName(adminFirstName);
            admin.setLastName(adminLastName);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            admin.setStatus(UserStatus.ACTIVE);
            userRepository.save(admin);
            log.info("Admin user created: {}", adminEmail);
        }
    }
}
