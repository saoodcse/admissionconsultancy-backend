package com.admissionconsultancy.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class TokenRefreshAdvice implements ResponseBodyAdvice<Object> {

    private final JwtService jwtService;

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {

        String path = request.getURI().getPath();

        // Only refresh tokens on secure admin routes
        if (path == null || !path.contains("/admin")) {
            return body;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null &&
                authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken)) {

            try {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                String freshToken = jwtService.generateToken(userDetails);

                response.getHeaders().add("Access-Control-Expose-Headers", "X-New-Token");
                response.getHeaders().add("X-New-Token", "Bearer " + freshToken);

                log.debug("Fresh token attached for user: {} on path: {}", userDetails.getUsername(), path);
            } catch (Exception e) {
                log.error("Failed to generate refresh token for path: {}", path, e);
            }
        }
        return body;
    }
}