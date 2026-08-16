package com.admissionconsultancy.config;

import com.admissionconsultancy.entity.AdminUser;
import com.admissionconsultancy.enums.Role;
import com.admissionconsultancy.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AdminUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("No admin user found. Seeding default SUPER_ADMIN...");

            AdminUser admin = new AdminUser();
            admin.setName("System Admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123")); // Raw password to login
            admin.setRoles(Set.of(Role.SUPER_ADMIN, Role.ADMIN));

            userRepository.save(admin);
            log.info("Default SUPER_ADMIN seeded successfully. You can now login with admin@example.com");
        }
    }
}