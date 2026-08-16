package com.admissionconsultancy.security;

import com.admissionconsultancy.repository.AdminUserRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.Collectors;

@Configuration
public class AppSecurityConfig {

    private final AdminUserRepository repository;

    // Timer for Authentication Performance
    private final Timer loadUserDetailsTimer;

    public AppSecurityConfig(AdminUserRepository repository, MeterRegistry meterRegistry) {
        this.repository = repository;

        // Initialize Timer
        this.loadUserDetailsTimer = Timer.builder("security.user_details.load.time")
                .description("Time taken by Spring Security to load user details from the database")
                .register(meterRegistry);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username ->
                // Record the exact time it takes to fetch and map the user during auth
                loadUserDetailsTimer.record(() ->
                        repository.findByEmailAndIsActiveTrue(username)
                                .map(admin -> {
                                    var authorities = admin.getRoles().stream()
                                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                                            .collect(Collectors.toList());

                                    return User.builder()
                                            .username(admin.getEmail())
                                            .password(admin.getPassword())
                                            .authorities(authorities)
                                            .build();
                                })
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"))
                );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        // Pass userDetailsService directly into the constructor
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        // Keep the password encoder setter
        authProvider.setPasswordEncoder(passwordEncoder);

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}