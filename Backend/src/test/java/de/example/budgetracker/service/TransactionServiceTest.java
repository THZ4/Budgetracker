package de.example.budgetracker.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.example.budgetracker.model.Transaction;
import de.example.budgetracker.repository.TransactionRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

public class TransactionServiceTest {

  @Mock
  private TransactionRepository repository;

  @InjectMocks
  private TransactionService service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getAll_shouldReturnAllTransactions() {
    Transaction t1 = new Transaction();
    Transaction t2 = new Transaction();
    when(repository.findAll()).thenReturn(List.of(t1, t2));

    List<Transaction> result = service.getAll();

    assertEquals(2, result.size());
    verify(repository, times(1)).findAll();
  }

  @Test
  void save_shouldReturnSavedTransaction() {
    Transaction tx = new Transaction();
    tx.setAmount(100.0);

    when(repository.save(tx)).thenReturn(tx);

    Transaction result = service.save(tx);

    assertEquals(100.0, result.getAmount());
    verify(repository).save(tx);
  }

  @Test
  void deleteById_shouldInvokeRepositoryDelete() {
    Long id = 1L;

    service.deleteById(id);

    verify(repository).deleteById(id);
  }

  @Test
  void getAllForUser_shouldReturnUserTransactions() {
    String username = "testuser";
    Transaction tx1 = new Transaction();
    Transaction tx2 = new Transaction();

    when(repository.findByUserUsername(username)).thenReturn(List.of(tx1, tx2));

    List<Transaction> result = service.getAllForUser(username);

    assertEquals(2, result.size());
    verify(repository).findByUserUsername(username);
  }
}
