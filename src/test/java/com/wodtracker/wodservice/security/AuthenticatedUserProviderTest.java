package com.wodtracker.wodservice.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticatedUserProviderTest {

    private final AuthenticatedUserProvider authenticatedUserProvider = new AuthenticatedUserProvider();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReadAuthenticatedUserFromSecurityContext() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("email", "admin@example.com")
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        jwt,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"))
                ) {
                    @Override
                    public String getName() {
                        return "7";
                    }
                }
        );

        AuthenticatedUser user = authenticatedUserProvider.getAuthenticatedUser();

        assertThat(user.userId()).isEqualTo(7L);
        assertThat(user.email()).isEqualTo("admin@example.com");
        assertThat(user.roles()).containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
        assertThat(user.isAdmin()).isTrue();
    }
}
