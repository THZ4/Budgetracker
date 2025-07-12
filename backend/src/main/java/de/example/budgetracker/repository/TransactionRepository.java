package de.example.budgetracker.repository;

import de.example.budgetracker.model.Transaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository
  extends JpaRepository<Transaction, Long> {
  List<Transaction> findByUserUsername(String username);
}
