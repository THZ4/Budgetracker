package de.example.budgetracker.service;

import de.example.budgetracker.model.User;
import de.example.budgetracker.repository.UserRepository;
import de.example.budgetracker.util.JwtUtil;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  @Autowired
  private UserRepository userRepo;

  @Autowired
  private PasswordEncoder passwordEncoder;

  public void register(String username, String password) {
    if (userRepo.findByUsername(username).isPresent()) {
      throw new RuntimeException("User already exists");
    }
    User user = new User();
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(password));
    userRepo.save(user);
  }

  @Autowired
  private JwtUtil jwtUtil;

  public String login(String username, String password) {
    Optional<User> optionalUser = userRepo.findByUsername(username);
    if (
      optionalUser.isPresent() &&
      passwordEncoder.matches(password, optionalUser.get().getPassword())
    ) {
      return jwtUtil.generateToken(username);
    }
    throw new RuntimeException("Invalid credentials");
  }
}
