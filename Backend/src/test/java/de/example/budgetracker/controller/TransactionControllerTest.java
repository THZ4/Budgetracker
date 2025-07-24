package de.example.budgetracker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.example.budgetracker.Controller.TransactionController;
import de.example.budgetracker.model.Transaction;
import de.example.budgetracker.model.User;
import de.example.budgetracker.repository.UserRepository;
import de.example.budgetracker.service.TransactionService;
import de.example.budgetracker.util.JwtUtil;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
  controllers = TransactionController.class,
  excludeAutoConfiguration = SecurityAutoConfiguration.class
)
public class TransactionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TransactionService service;

  @MockBean
  private UserRepository userRepository;

  @MockBean
  private JwtUtil jwtUtil;

  @Autowired
  private ObjectMapper objectMapper;

  private User testUser;
  private Transaction transaction1;
  private Transaction transaction2;

  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setId(1L);
    testUser.setUsername("testuser");
    testUser.setPassword("password");

    transaction1 = new Transaction();
    transaction1.setId(1L);
    transaction1.setDescription("Grocery Shopping");
    transaction1.setAmount(50.00);
    transaction1.setDate(LocalDate.of(2023, 1, 15));
    transaction1.setType("Expense");
    transaction1.setUser(testUser);

    transaction2 = new Transaction();
    transaction2.setId(2L);
    transaction2.setDescription("Salary");
    transaction2.setAmount(2000.00);
    transaction2.setDate(LocalDate.of(2023, 1, 31));
    transaction2.setType("Income");
    transaction2.setUser(testUser);
  }

  @Test
  @WithMockUser(username = "testuser")
  void getAll_shouldReturnTransactionsForUser() throws Exception {
    List<Transaction> allTransactions = Arrays.asList(
      transaction1,
      transaction2
    );
    when(service.getAllForUser(anyString())).thenReturn(allTransactions);

    mockMvc
      .perform(get("/api/transactions"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].description").value("Grocery Shopping"))
      .andExpect(jsonPath("$[1].description").value("Salary"));
  }

  @Test
  @WithMockUser(username = "testuser")
  void create_shouldCreateNewTransaction() throws Exception {
    Transaction newTransaction = new Transaction();
    newTransaction.setDescription("New Purchase");
    newTransaction.setAmount(25.00);
    newTransaction.setDate(LocalDate.of(2023, 2, 1));
    newTransaction.setType("Expense");
    newTransaction.setId(99L);

    Transaction savedTransaction = new Transaction();
    savedTransaction.setId(3L);
    savedTransaction.setDescription("New Purchase");
    savedTransaction.setAmount(25.00);
    savedTransaction.setDate(LocalDate.of(2023, 2, 1));
    savedTransaction.setType("Expense");
    savedTransaction.setUser(testUser);

    when(userRepository.findByUsername(anyString()))
      .thenReturn(Optional.of(testUser));
    when(service.save(any(Transaction.class))).thenReturn(savedTransaction);

    mockMvc
      .perform(
        post("/api/transactions")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(newTransaction))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(3L))
      .andExpect(jsonPath("$.description").value("New Purchase"));
  }

  @Test
  @WithMockUser(username = "testuser")
  void delete_shouldDeleteTransactionById() throws Exception {
    doNothing().when(service).deleteById(anyLong());

    mockMvc
      .perform(delete("/api/transactions/{id}", 1L))
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "testuser")
  void update_shouldUpdateExistingTransaction() throws Exception {
    Transaction updatedTransaction = new Transaction();
    updatedTransaction.setDescription("Updated Grocery Shopping");
    updatedTransaction.setAmount(60.00);
    updatedTransaction.setDate(LocalDate.of(2023, 1, 15));
    updatedTransaction.setType("Expense");
    updatedTransaction.setId(1L);

    when(service.save(any(Transaction.class))).thenReturn(updatedTransaction);

    mockMvc
      .perform(
        put("/api/transactions/{id}", 1L)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(updatedTransaction))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1L))
      .andExpect(jsonPath("$.description").value("Updated Grocery Shopping"))
      .andExpect(jsonPath("$.amount").value(60.00));
  }
}
