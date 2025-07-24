package de.example.budgetracker.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JwtUtilTest {

  private JwtUtil jwtUtil;

  @BeforeEach
  void setUp() {
    jwtUtil = new JwtUtil();
  }

  @Test
  void generateToken_shouldContainCorrectUsername() {
    String username = "testuser";
    String token = jwtUtil.generateToken(username);

    assertNotNull(token);
    String extractedUsername = jwtUtil.extractUsername(token);
    assertEquals(username, extractedUsername);
  }

  @Test
  void validateToken_shouldReturnTrue_forValidToken() {
    String token = jwtUtil.generateToken("testuser");

    assertTrue(jwtUtil.validateToken(token));
  }

  @Test
  void validateToken_shouldReturnFalse_forInvalidToken() {
    String invalidToken = "invalid.jwt.token";

    assertFalse(jwtUtil.validateToken(invalidToken));
  }

  @Test
  void extractUsername_shouldThrowException_forInvalidToken() {
    String invalidToken = "invalid.jwt.token";

    assertThrows(Exception.class, () -> jwtUtil.extractUsername(invalidToken));
  }
}
