package de.example.budgetracker.service;

import de.example.budgetracker.model.Transaction;
import de.example.budgetracker.repository.TransactionRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

  @Autowired
  private TransactionRepository repository;

  public List<Transaction> getAll() {
    return repository.findAll();
  }

  public Transaction save(Transaction tx) {
    return repository.save(tx);
  }

  public void deleteById(Long id) {
    repository.deleteById(id);
  }

  public List<Transaction> getAllForUser(String username) {
    return repository.findByUserUsername(username);
  }
}
