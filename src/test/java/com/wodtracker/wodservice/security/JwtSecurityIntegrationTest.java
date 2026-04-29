package com.wodtracker.wodservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wodtracker.wodservice.dto.request.ResultRequestDTO;
import com.wodtracker.wodservice.dto.response.ResultResponseDTO;
import com.wodtracker.wodservice.dto.response.WodResponseDTO;
import com.wodtracker.wodservice.service.ResultService;
import com.wodtracker.wodservice.service.WodService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JwtSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WodService wodService;

    @MockitoBean
    private ResultService resultService;

    @Test
    void shouldRejectProtectedEndpointWithoutJwt() throws Exception {
        mockMvc.perform(get("/wods"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("No autenticado"))
                .andExpect(jsonPath("$.message").value("Debes iniciar sesión para acceder a este recurso."));
    }

    @Test
    void shouldAllowAuthenticatedUserToGetTodayWod() throws Exception {
        when(wodService.getTodayWod()).thenReturn(
                new WodResponseDTO(1L, "Fran", "21-15-9 thrusters and pull-ups", null, LocalDate.now(), true)
        );

        mockMvc.perform(get("/wods/today")
                        .with(userJwt(7L, "athlete@example.com", "USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Fran"));
    }

    @Test
    void shouldForbidAdminEndpointForUserRole() throws Exception {
        mockMvc.perform(post("/wods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Murph",
                                  "description": "For time",
                                  "type": "FOR_TIME",
                                  "date": "%s",
                                  "approved": true
                }
                                """.formatted(LocalDate.now()))
                        .with(userJwt(7L, "athlete@example.com", "USER")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Acceso denegado"))
                .andExpect(jsonPath("$.message").value("No tienes permisos para acceder a este recurso."));
    }

    @Test
    void shouldAllowAdminToCreateWod() throws Exception {
        when(wodService.createWod(any())).thenReturn(
                new WodResponseDTO(1L, "Murph", "For time", null, LocalDate.now(), true)
        );

        String payload = """
                {
                  "name": "Murph",
                  "description": "For time",
                  "type": "FOR_TIME",
                  "date": "%s",
                  "approved": true
                }
                """.formatted(LocalDate.now());

        mockMvc.perform(post("/wods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
                        .with(userJwt(1L, "admin@example.com", "ADMIN")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Murph"));
    }

    @Test
    void shouldAllowAuthenticatedUserToCreateResult() throws Exception {
        when(resultService.createResult(any())).thenReturn(
                new ResultResponseDTO(1L, 7L, 10L, "Fran", "21-15-9 thrusters and pull-ups", "03:45", LocalDateTime.now())
        );

        ResultRequestDTO requestDTO = new ResultRequestDTO(10L, "03:45");

        mockMvc.perform(post("/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .with(userJwt(7L, "athlete@example.com", "USER")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(7L));
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor userJwt(Long userId, String email, String role) {
        return jwt()
                .jwt(jwt -> jwt
                        .subject(String.valueOf(userId))
                        .claim("email", email)
                        .claim("roles", List.of(role)))
                .authorities(new SimpleGrantedAuthority("ROLE_" + role));
    }
}
