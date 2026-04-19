package com.wodtracker.wodservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // NOSONAR: Stateless JWT bearer API without cookie/session authentication.
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/health",
                                "/h2-console/**",
                                "/error",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/wods").authenticated()
                        .requestMatchers(HttpMethod.GET, "/wods/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/wods/today").authenticated()
                        .requestMatchers(HttpMethod.POST, "/wods").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/wods/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/wods/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/results").authenticated()
                        .requestMatchers(HttpMethod.GET, "/results/user/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/results/wod/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/proposals").authenticated()
                        .requestMatchers(HttpMethod.GET, "/proposals/pending").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/proposals/*/approve").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/proposals/*/reject").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return authenticationConverter;
    }
}
