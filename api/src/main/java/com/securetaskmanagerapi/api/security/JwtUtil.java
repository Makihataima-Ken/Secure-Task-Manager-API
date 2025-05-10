package com.securetaskmanagerapi.api.security;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    public String extractUserId(Jwt jwt) {
        return jwt.getSubject(); // "sub" claim from Keycloak token
    }
}

