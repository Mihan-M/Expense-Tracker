package com.slt.expense_tracker.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.slt.expense_tracker.dto.ExpenseRequest;
import com.slt.expense_tracker.dto.LoginRequest;
import com.slt.expense_tracker.dto.RegisterRequest;
import com.slt.expense_tracker.entity.ExpenseCategory;
import com.slt.expense_tracker.repository.ExpenseRepository;
import com.slt.expense_tracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ExpenseIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private MockMvc mockMvc;
    private String userAToken;
    private String userBToken;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        expenseRepository.deleteAll();
        userRepository.deleteAll();

        userAToken = registerAndLogin("User A", "usera@example.com", "SecurePass@123");
        userBToken = registerAndLogin("User B", "userb@example.com", "SecurePass@123");
    }

    private String registerAndLogin(String name, String email, String password) throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName(name);
        registerRequest.setEmail(email);
        registerRequest.setAddress("123 Street");
        registerRequest.setPassword(password);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseStr = result.getResponse().getContentAsString();
        return objectMapper.readTree(responseStr).get("token").asText();
    }

    @Test
    @DisplayName("Full Expense CRUD lifecycle")
    void testExpenseCrudLifecycle() throws Exception {
        ExpenseRequest createReq = ExpenseRequest.builder()
                .title("Groceries")
                .category(ExpenseCategory.FOOD)
                .amount(new BigDecimal("150.00"))
                .transactionDate(LocalDate.now().minusDays(1))
                .note("Weekly groceries")
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/expenses")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Groceries"))
                .andExpect(jsonPath("$.amount").value(150.00))
                .andReturn();

        Long expenseId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/api/expenses")
                        .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Groceries"));

        ExpenseRequest updateReq = ExpenseRequest.builder()
                .title("Supermarket Groceries")
                .category(ExpenseCategory.FOOD)
                .amount(new BigDecimal("180.00"))
                .transactionDate(LocalDate.now())
                .note("Updated groceries")
                .build();

        mockMvc.perform(put("/api/expenses/" + expenseId)
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Supermarket Groceries"))
                .andExpect(jsonPath("$.amount").value(180.00));

        mockMvc.perform(delete("/api/expenses/" + expenseId)
                        .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/expenses")
                        .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("User B cannot access or modify User A's expense (Ownership Isolation)")
    void testOwnershipIsolation() throws Exception {
        ExpenseRequest createReq = ExpenseRequest.builder()
                .title("User A Private Expense")
                .category(ExpenseCategory.SHOPPING)
                .amount(new BigDecimal("75.00"))
                .transactionDate(LocalDate.now())
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/expenses")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Long expenseId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/api/expenses")
                        .header("Authorization", "Bearer " + userBToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        ExpenseRequest updateReq = ExpenseRequest.builder()
                .title("Hacked Title")
                .category(ExpenseCategory.SHOPPING)
                .amount(new BigDecimal("1.00"))
                .transactionDate(LocalDate.now())
                .build();

        mockMvc.perform(put("/api/expenses/" + expenseId)
                        .header("Authorization", "Bearer " + userBToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/expenses/" + expenseId)
                        .header("Authorization", "Bearer " + userBToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Creating an expense with a future date returns 400 validation error")
    void testCreateExpense_FutureDate_BadRequest() throws Exception {
        ExpenseRequest futureReq = ExpenseRequest.builder()
                .title("Future Expense")
                .category(ExpenseCategory.BILLS)
                .amount(new BigDecimal("200.00"))
                .transactionDate(LocalDate.now().plusDays(5))
                .build();

        mockMvc.perform(post("/api/expenses")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(futureReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.transactionDate").exists());
    }
}
