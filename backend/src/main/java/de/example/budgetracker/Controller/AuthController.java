package de.example.budgetracker.Controller;

import de.example.budgetracker.service.AuthService;
import java.util.Map;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  private AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody AuthRequest request) {
    authService.register(request.getUsername(), request.getPassword());
    return ResponseEntity.ok(Map.of("message", "User registered"));
  }

  // prettier-ignore
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody AuthRequest request) {
    try {
      String token = authService.login(request.getUsername(), request.getPassword());
      return ResponseEntity.ok(Map.of("token", token));
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
    }
  }
}

@Data
class AuthRequest {

  private String username;
  private String password;
}
