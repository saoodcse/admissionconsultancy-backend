package com.admissionconsultancy.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenRefreshAdviceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private ServerHttpResponse response;

    private HttpHeaders httpHeaders;

    @InjectMocks
    private TokenRefreshAdvice tokenRefreshAdvice;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        httpHeaders = new HttpHeaders();
        lenient().when(response.getHeaders()).thenReturn(httpHeaders);

        userDetails = new User("admin@admission.com", "password", AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should return true for supports method")
    void testSupports() {
        boolean supports = tokenRefreshAdvice.supports(null, null);
        assertThat(supports).isTrue();
    }

    @Test
    @DisplayName("Should attach X-New-Token header on secure admin route for authenticated user")
    void testBeforeBodyWrite_AdminRoute_AuthenticatedUser() throws Exception {
        when(request.getURI()).thenReturn(new URI("http://localhost:8080/api/admin/users"));
        when(jwtService.generateToken(userDetails)).thenReturn("new_refreshed_jwt_token");

        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Object body = "Response Payload";
        Object result = tokenRefreshAdvice.beforeBodyWrite(body, null, MediaType.APPLICATION_JSON, null, request, response);

        assertThat(result).isEqualTo(body);
        assertThat(httpHeaders.getFirst("Access-Control-Expose-Headers")).isEqualTo("X-New-Token");
        assertThat(httpHeaders.getFirst("X-New-Token")).isEqualTo("Bearer new_refreshed_jwt_token");

        verify(jwtService, times(1)).generateToken(userDetails);
    }

    @Test
    @DisplayName("Should ignore and not attach token on non-admin routes")
    void testBeforeBodyWrite_NonAdminRoute() throws Exception {
        when(request.getURI()).thenReturn(new URI("http://localhost:8080/api/public/courses"));

        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Object body = "Public Payload";
        Object result = tokenRefreshAdvice.beforeBodyWrite(body, null, MediaType.APPLICATION_JSON, null, request, response);

        assertThat(result).isEqualTo(body);
        assertThat(httpHeaders.containsHeader("X-New-Token")).isFalse();
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    @DisplayName("Should ignore if authentication is anonymous")
    void testBeforeBodyWrite_AnonymousUser() throws Exception {
        when(request.getURI()).thenReturn(new URI("http://localhost:8080/api/admin/dashboard"));

        AnonymousAuthenticationToken anonymousAuth = new AnonymousAuthenticationToken(
                "key", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")
        );
        SecurityContextHolder.getContext().setAuthentication(anonymousAuth);

        Object body = "Dashboard Data";
        Object result = tokenRefreshAdvice.beforeBodyWrite(body, null, MediaType.APPLICATION_JSON, null, request, response);

        assertThat(result).isEqualTo(body);
        assertThat(httpHeaders.containsHeader("X-New-Token")).isFalse();
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    @DisplayName("Should handle exceptions safely if token generation fails during refresh")
    void testBeforeBodyWrite_JwtGenerationException() throws Exception {
        when(request.getURI()).thenReturn(new URI("http://localhost:8080/api/admin/settings"));
        when(jwtService.generateToken(userDetails)).thenThrow(new RuntimeException("Token generation error"));

        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Object body = "Settings Data";
        Object result = tokenRefreshAdvice.beforeBodyWrite(body, null, MediaType.APPLICATION_JSON, null, request, response);

        assertThat(result).isEqualTo(body);
        assertThat(httpHeaders.containsHeader("X-New-Token")).isFalse();
    }
}