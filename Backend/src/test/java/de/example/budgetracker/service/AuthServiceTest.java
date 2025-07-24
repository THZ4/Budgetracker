package de.example.budgetracker.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import de.example.budgetracker.model.User;
import de.example.budgetracker.repository.UserRepository;
import de.example.budgetracker.util.JwtUtil;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthServiceTest {

  @Mock
  private UserRepository userRepo;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtUtil jwtUtil;

  @InjectMocks
  private AuthService authService;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void register_shouldSaveNewUser() {
    String username = "newuser";
    String rawPassword = "pass123";
    String encodedPassword = "encodedPass";

    when(userRepo.findByUsername(username)).thenReturn(Optional.empty());
    when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

    authService.register(username, rawPassword);

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepo).save(userCaptor.capture());

    User savedUser = userCaptor.getValue();
    assertEquals(username, savedUser.getUsername());
    assertEquals(encodedPassword, savedUser.getPassword());
  }

  @Test
  void register_shouldThrowIfUserExists() {
    String username = "existing";
    when(userRepo.findByUsername(username)).thenReturn(Optional.of(new User()));

    RuntimeException ex = assertThrows(
      RuntimeException.class,
      () -> authService.register(username, "pass")
    );
    assertEquals("User already exists", ex.getMessage());
    verify(userRepo, never()).save(any());
  }

  @Test
  void login_shouldReturnJwtTokenOnSuccess() {
    String username = "test";
    String rawPassword = "secret";
    String encodedPassword = "encodedSecret";
    String token = "jwt.token";

    User user = new User();
    user.setUsername(username);
    user.setPassword(encodedPassword);

    when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(rawPassword, encodedPassword))
      .thenReturn(true);
    when(jwtUtil.generateToken(username)).thenReturn(token);

    String result = authService.login(username, rawPassword);

    assertEquals(token, result);
  }

  @Test
  void login_shouldThrowOnInvalidCredentials() {
    String username = "user";
    String password = "wrong";

    when(userRepo.findByUsername(username)).thenReturn(Optional.empty());

    assertThrows(
      RuntimeException.class,
      () -> authService.login(username, password)
    );
  }

  @Test
  void login_shouldThrowIfPasswordWrong() {
    String username = "user";
    String password = "wrong";
    String encoded = "encoded";

    User user = new User();
    user.setUsername(username);
    user.setPassword(encoded);

    when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(password, encoded)).thenReturn(false);

    assertThrows(
      RuntimeException.class,
      () -> authService.login(username, password)
    );
  }
}
