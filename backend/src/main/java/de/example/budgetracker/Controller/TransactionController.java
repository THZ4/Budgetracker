package de.example.budgetracker.Controller;

import de.example.budgetracker.model.Transaction;
import de.example.budgetracker.model.User;
import de.example.budgetracker.repository.UserRepository;
import de.example.budgetracker.service.TransactionService;
import java.util.List;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:4200") // Angular-Port
public class TransactionController {

  private final TransactionService service;
  private final UserRepository userRepository;

  public TransactionController(
    TransactionService service,
    UserRepository userRepository
  ) {
    this.service = service;
    this.userRepository = userRepository;
  }

  @GetMapping
  public List<Transaction> getAll() {
    String username = SecurityContextHolder
      .getContext()
      .getAuthentication()
      .getName();
    return service.getAllForUser(username);
  }

  @PostMapping
  public Transaction create(@RequestBody Transaction tx) {
    String username = SecurityContextHolder
      .getContext()
      .getAuthentication()
      .getName();

    User user = userRepository.findByUsername(username).orElseThrow();

    tx.setUser(user);
    tx.setId(null); // falls Angular eine ID mitsendet

    return service.save(tx);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    service.deleteById(id);
  }

  @PutMapping("/{id}")
  public Transaction update(
    @PathVariable Long id,
    @RequestBody Transaction tx
  ) {
    tx.setId(id);
    return service.save(tx);
  }
}
