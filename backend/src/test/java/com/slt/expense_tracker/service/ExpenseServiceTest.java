package com.slt.expense_tracker.service;

import com.slt.expense_tracker.dto.ExpenseRequest;
import com.slt.expense_tracker.dto.ExpenseResponse;
import com.slt.expense_tracker.entity.Expense;
import com.slt.expense_tracker.entity.ExpenseCategory;
import com.slt.expense_tracker.entity.User;
import com.slt.expense_tracker.exception.ResourceNotFoundException;
import com.slt.expense_tracker.exception.UnauthorizedAccessException;
import com.slt.expense_tracker.repository.ExpenseRepository;
import com.slt.expense_tracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExpenseService expenseService;

    private User user1;
    private User user2;
    private Expense expense1;
    private ExpenseRequest expenseRequest;

    @BeforeEach
    void setUp() {
        user1 = User.builder().id(1L).email("user1@example.com").name("User One").build();
        user2 = User.builder().id(2L).email("user2@example.com").name("User Two").build();

        expense1 = Expense.builder()
                .id(100L)
                .title("Lunch")
                .category(ExpenseCategory.FOOD)
                .amount(new BigDecimal("15.50"))
                .transactionDate(LocalDate.now())
                .note("Team lunch")
                .user(user1)
                .build();

        expenseRequest = ExpenseRequest.builder()
                .title("Lunch")
                .category(ExpenseCategory.FOOD)
                .amount(new BigDecimal("15.50"))
                .transactionDate(LocalDate.now())
                .note("Team lunch")
                .build();
    }

    @Test
    @DisplayName("Should create expense for authenticated user")
    void testCreateExpense_Success() {
        when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense1);

        ExpenseResponse response = expenseService.createExpense(expenseRequest, user1.getEmail());

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("Lunch", response.getTitle());
        assertEquals(ExpenseCategory.FOOD, response.getCategory());
        assertEquals(new BigDecimal("15.50"), response.getAmount());
        assertEquals(1L, response.getUserId());
    }

    @Test
    @DisplayName("Should fetch expenses for authenticated user")
    void testGetExpenses_Success() {
        when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        when(expenseRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of(expense1));

        List<ExpenseResponse> results = expenseService.getExpenses(
                user1.getEmail(), null, null, null, null
        );

        assertEquals(1, results.size());
        assertEquals("Lunch", results.get(0).getTitle());
    }

    @Test
    @DisplayName("Should update expense successfully when owned by user")
    void testUpdateExpense_Success() {
        when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        when(expenseRepository.findById(100L)).thenReturn(Optional.of(expense1));
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense1);

        ExpenseRequest updateReq = ExpenseRequest.builder()
                .title("Updated Lunch")
                .category(ExpenseCategory.FOOD)
                .amount(new BigDecimal("20.00"))
                .transactionDate(LocalDate.now())
                .note("Updated note")
                .build();

        ExpenseResponse response = expenseService.updateExpense(100L, updateReq, user1.getEmail());

        assertNotNull(response);
        verify(expenseRepository, times(1)).save(expense1);
    }

    @Test
    @DisplayName("Should throw UnauthorizedAccessException when updating another user's expense")
    void testUpdateExpense_Unauthorized() {
        when(userRepository.findByEmail(user2.getEmail())).thenReturn(Optional.of(user2));
        when(expenseRepository.findById(100L)).thenReturn(Optional.of(expense1)); // expense belongs to user1

        assertThrows(UnauthorizedAccessException.class, () ->
                expenseService.updateExpense(100L, expenseRequest, user2.getEmail())
        );

        verify(expenseRepository, never()).save(any(Expense.class));
    }

    @Test
    @DisplayName("Should delete expense successfully when owned by user")
    void testDeleteExpense_Success() {
        when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        when(expenseRepository.findById(100L)).thenReturn(Optional.of(expense1));

        expenseService.deleteExpense(100L, user1.getEmail());

        verify(expenseRepository, times(1)).delete(expense1);
    }

    @Test
    @DisplayName("Should throw UnauthorizedAccessException when deleting another user's expense")
    void testDeleteExpense_Unauthorized() {
        when(userRepository.findByEmail(user2.getEmail())).thenReturn(Optional.of(user2));
        when(expenseRepository.findById(100L)).thenReturn(Optional.of(expense1)); // expense belongs to user1

        assertThrows(UnauthorizedAccessException.class, () ->
                expenseService.deleteExpense(100L, user2.getEmail())
        );

        verify(expenseRepository, never()).delete(any(Expense.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when expense ID does not exist")
    void testGetExpense_NotFound() {
        when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        when(expenseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                expenseService.deleteExpense(999L, user1.getEmail())
        );
    }
}
