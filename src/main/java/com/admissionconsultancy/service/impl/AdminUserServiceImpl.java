package com.admissionconsultancy.service.impl;

import com.admissionconsultancy.constants.AppCode;
import com.admissionconsultancy.dto.CreateUserRequestDTO;
import com.admissionconsultancy.dto.UserResponseDTO;
import com.admissionconsultancy.entity.AdminUser;
import com.admissionconsultancy.exception.DuplicateResourceException;
import com.admissionconsultancy.exception.ResourceNotFoundException;
import com.admissionconsultancy.repository.AdminUserRepository;
import com.admissionconsultancy.service.AdminUserService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminUserServiceImpl implements AdminUserService {

    private final AdminUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Counters for Business Operations
    private final Counter adminUserCreatedCounter;

    // Timers for Performance and DB Metrics
    private final Timer existsByEmailTimer;
    private final Timer saveDbQueryTimer;
    private final Timer fetchAllQueryTimer;
    private final Timer passwordEncodeTimer;

    public AdminUserServiceImpl(
            AdminUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            MeterRegistry meterRegistry) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

        // Initialize Business Metric
        this.adminUserCreatedCounter = Counter.builder("business.admin_user.created.total")
                .description("Total number of admin users created")
                .register(meterRegistry);

        // Initialize DB Performance Timers
        this.existsByEmailTimer = Timer.builder("db.query.admin_user.exists_by_email.time")
                .description("Time taken to check if an admin user exists by email")
                .register(meterRegistry);

        this.saveDbQueryTimer = Timer.builder("db.query.admin_user.save.time")
                .description("Time taken to persist a new admin user")
                .register(meterRegistry);

        this.fetchAllQueryTimer = Timer.builder("db.query.admin_user.fetch_all.time")
                .description("Time taken to fetch all admin users")
                .register(meterRegistry);

        // Initialize Security Performance Timer
        this.passwordEncodeTimer = Timer.builder("auth.password.encode.time")
                .description("Time taken to hash the user's password using BCrypt")
                .register(meterRegistry);
    }

    @Override
    @Transactional
    public UserResponseDTO createUser(CreateUserRequestDTO request) {
        log.info("Creating new admin user: {}", request.getEmail());

        // Record Exact DB Query Time
        boolean exists = existsByEmailTimer.record(() -> userRepository.existsByEmail(request.getEmail()));

        if (exists) {
            log.error("Creation failed. User email '{}' already exists.", request.getEmail());
            throw new DuplicateResourceException(AppCode.BAD_REQUEST);
        }

        AdminUser newUser = new AdminUser();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());

        // Record Time taken for BCrypt hashing (CPU intensive operation)
        String encodedPassword = passwordEncodeTimer.record(() -> passwordEncoder.encode(request.getPassword()));
        newUser.setPassword(encodedPassword);

        newUser.setRoles(request.getRoles());
        newUser.setActive(true);

        // Record Exact DB Insertion Time
        AdminUser savedUser = saveDbQueryTimer.record(() -> userRepository.save(newUser));

        log.info("Successfully created admin user ID: {}", savedUser.getId());

        // Increment Business Metric
        adminUserCreatedCounter.increment();

        return mapToDTO(savedUser);
    }

    @Override
    public List<UserResponseDTO> getAllActiveUsers() {
        log.info("Fetching all active admin users");

        // Record Exact DB Query Time
        List<AdminUser> allUsers = fetchAllQueryTimer.record(()->userRepository.findAll());

        return allUsers.stream()
                .filter(AdminUser::isActive)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getUserByEmail(String email) {
        log.info("Fetching admin user: {}", email);

        AdminUser user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new ResourceNotFoundException(AppCode.RESOURCE_NOT_FOUND));

        return mapToDTO(user);
    }

    @Override
    @Transactional
    public void deleteUser(String email) {
        log.info("Deactivating admin user: {}", email);

        int updated = userRepository.deactivateByEmail(email);

        if (updated == 0) {
            throw new ResourceNotFoundException(AppCode.RESOURCE_NOT_FOUND);
        }

        log.info("Successfully deactivated admin user: {}", email);
    }


    private UserResponseDTO mapToDTO(AdminUser user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles());
        dto.setActive(user.isActive());
        return dto;
    }
}