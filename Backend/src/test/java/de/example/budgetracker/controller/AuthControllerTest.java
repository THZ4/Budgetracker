package de.example.budgetracker.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.example.budgetracker.Controller.AuthController;
import de.example.budgetracker.service.AuthService;
import de.example.budgetracker.util.JwtUtil;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
  controllers = AuthController.class,
  excludeAutoConfiguration = SecurityAutoConfiguration.class
)
public class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AuthService authService;

  @MockBean
  private JwtUtil jwtUtil;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void register_shouldReturnOkAndMessage() throws Exception {
    Map<String, String> request = Map.of(
      "username",
      "testuser",
      "password",
      "testpass"
    );

    doNothing().when(authService).register("testuser", "testpass");

    mockMvc
      .perform(
        post("/api/auth/register")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.message").value("User registered"));
  }

  @Test
  void login_shouldReturnToken_whenCredentialsValid() throws Exception {
    Map<String, String> request = Map.of(
      "username",
      "testuser",
      "password",
      "testpass"
    );

    when(authService.login("testuser", "testpass"))
      .thenReturn("mocked-jwt-token");

    mockMvc
      .perform(
        post("/api/auth/login")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.token").value("mocked-jwt-token"));
  }

  @Test
  void login_shouldReturnUnauthorized_whenCredentialsInvalid()
    throws Exception {
    Map<String, String> request = Map.of(
      "username",
      "wronguser",
      "password",
      "wrongpass"
    );

    when(authService.login("wronguser", "wrongpass"))
      .thenThrow(new RuntimeException("Invalid credentials"));

    mockMvc
      .perform(
        post("/api/auth/login")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
      )
      .andExpect(status().isUnauthorized())
      .andExpect(jsonPath("$.error").value("Invalid credentials"));
  }

  @Test
  void login_shouldReturnBadRequest_whenMissingFields() throws Exception {
    String invalidJson = "{\"username\": \"testuser\": \"passwo\"}"; // kein Passwort

    mockMvc
      .perform(
        post("/api/auth/login")
          .contentType(MediaType.APPLICATION_JSON)
          .content(invalidJson)
      )
      .andExpect(status().isBadRequest());
  }
}
