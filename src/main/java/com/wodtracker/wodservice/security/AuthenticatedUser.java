package com.wodtracker.wodservice.security;

import java.util.Set;

public record AuthenticatedUser(Long userId, String email, Set<String> roles) {

    public boolean isAdmin() {
        return roles.contains("ROLE_ADMIN");
    }
}
