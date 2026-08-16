package com.admissionconsultancy.security;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private MeterRegistry meterRegistry;
    private JwtService jwtService;
    private UserDetails userDetails;

    // Standard valid Base64 encoded test secret key
    private static final String TEST_SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        jwtService = new JwtService(TEST_SECRET, meterRegistry);

        userDetails = new User("admin@admission.com", "password", Collections.emptyList());
    }

    @Test
    @DisplayName("Should generate token successfully, increment generated counter, and record generation timer")
    void testGenerateToken_Success() {
        String token = jwtService.generateToken(userDetails);

        assertThat(token).isNotNull().isNotEmpty();
        assertThat(meterRegistry.get("security.jwt.generated.total").counter().count()).isEqualTo(1.0);
        assertThat(meterRegistry.get("security.jwt.generation.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should extract username from valid token successfully")
    void testExtractUsername() {
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("admin@admission.com");
    }

    @Test
    @DisplayName("Should return true for valid token and increment success validation counter")
    void testIsTokenValid_Success() {
        String token = jwtService.generateToken(userDetails);
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertThat(isValid).isTrue();
        assertThat(meterRegistry.get("security.jwt.validation.success.total").counter().count()).isEqualTo(1.0);
        // Assert greater than 0 since parsing timer gets invoked during username extraction + claim parsing
        assertThat(meterRegistry.get("security.jwt.parsing.time").timer().count()).isGreaterThan(0L);
    }

    @Test
    @DisplayName("Should return false and increment failed validation counter for mismatched user")
    void testIsTokenValid_UserMismatch() {
        String token = jwtService.generateToken(userDetails);
        UserDetails anotherUser = new User("other@admission.com", "password", Collections.emptyList());

        boolean isValid = jwtService.isTokenValid(token, anotherUser);

        assertThat(isValid).isFalse();
        assertThat(meterRegistry.get("security.jwt.validation.failed.total").counter().count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should throw exception and increment failed validation counter for malformed/tampered token")
    void testIsTokenValid_MalformedToken() {
        String malformedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.malformed.signature";

        assertThatThrownBy(() -> jwtService.isTokenValid(malformedToken, userDetails))
                .isInstanceOf(Exception.class);

        assertThat(meterRegistry.get("security.jwt.validation.failed.total").counter().count()).isEqualTo(1.0);
    }
}