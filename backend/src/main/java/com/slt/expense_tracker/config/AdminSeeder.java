package com.slt.expense_tracker.config;

import com.slt.expense_tracker.entity.Role;
import com.slt.expense_tracker.entity.User;
import com.slt.expense_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Value("${admin.email:admin@example.com}")
    private String adminEmail;

    @Value("${admin.password:Admin@12345}")
    private String adminPassword;

    public AdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder, JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        try {
            jdbcTemplate.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS role VARCHAR(20) DEFAULT 'USER' NOT NULL");
        } catch (Exception e) {
            // Suppress schema query failure if database dialect differs
        }

        try {
            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = User.builder()
                        .name("System Admin")
                        .email(adminEmail)
                        .address("System Head Office")
                        .password(passwordEncoder.encode(adminPassword))
                        .role(Role.ADMIN)
                        .build();
                userRepository.save(admin);
            }
        } catch (Exception ignored) {
        }
    }
}
