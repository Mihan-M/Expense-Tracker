package com.slt.expense_tracker.service;

import com.slt.expense_tracker.dto.IncomeRequest;
import com.slt.expense_tracker.dto.IncomeResponse;
import com.slt.expense_tracker.entity.Income;
import com.slt.expense_tracker.entity.User;
import com.slt.expense_tracker.exception.ResourceNotFoundException;
import com.slt.expense_tracker.exception.UnauthorizedAccessException;
import com.slt.expense_tracker.repository.IncomeRepository;
import com.slt.expense_tracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncomeServiceTest {

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private IncomeService incomeService;

    private User user1;
    private User user2;
    private Income income1;
    private IncomeRequest incomeRequest;

    @BeforeEach
    void setUp() {
        user1 = User.builder().id(1L).email("user1@example.com").name("User One").build();
        user2 = User.builder().id(2L).email("user2@example.com").name("User Two").build();

        income1 = Income.builder()
                .id(200L)
                .title("Salary")
                .amount(new BigDecimal("5000.00"))
                .incomeDate(LocalDate.now())
                .note("Monthly salary")
                .user(user1)
                .build();

        incomeRequest = IncomeRequest.builder()
                .title("Salary")
                .amount(new BigDecimal("5000.00"))
                .incomeDate(LocalDate.now())
                .note("Monthly salary")
                .build();
    }

    @Test
    @DisplayName("Should create income for authenticated user")
    void testCreateIncome_Success() {
        when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        when(incomeRepository.save(any(Income.class))).thenReturn(income1);

        IncomeResponse response = incomeService.createIncome(incomeRequest, user1.getEmail());

        assertNotNull(response);
        assertEquals(200L, response.getId());
        assertEquals("Salary", response.getTitle());
        assertEquals(new BigDecimal("5000.00"), response.getAmount());
        assertEquals(1L, response.getUserId());
    }

    @Test
    @DisplayName("Should fetch income list for authenticated user")
    void testGetIncomes_Success() {
        when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        when(incomeRepository.findByUser(eq(user1), any(Sort.class))).thenReturn(List.of(income1));

        List<IncomeResponse> results = incomeService.getIncomes(user1.getEmail());

        assertEquals(1, results.size());
        assertEquals("Salary", results.get(0).getTitle());
    }

    @Test
    @DisplayName("Should update income successfully when owned by user")
    void testUpdateIncome_Success() {
        when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        when(incomeRepository.findById(200L)).thenReturn(Optional.of(income1));
        when(incomeRepository.save(any(Income.class))).thenReturn(income1);

        IncomeRequest updateReq = IncomeRequest.builder()
                .title("Updated Salary")
                .amount(new BigDecimal("5500.00"))
                .incomeDate(LocalDate.now())
                .note("Bonus added")
                .build();

        IncomeResponse response = incomeService.updateIncome(200L, updateReq, user1.getEmail());

        assertNotNull(response);
        verify(incomeRepository, times(1)).save(income1);
    }

    @Test
    @DisplayName("Should throw UnauthorizedAccessException when updating another user's income")
    void testUpdateIncome_Unauthorized() {
        when(userRepository.findByEmail(user2.getEmail())).thenReturn(Optional.of(user2));
        when(incomeRepository.findById(200L)).thenReturn(Optional.of(income1));

        assertThrows(UnauthorizedAccessException.class, () ->
                incomeService.updateIncome(200L, incomeRequest, user2.getEmail())
        );

        verify(incomeRepository, never()).save(any(Income.class));
    }

    @Test
    @DisplayName("Should delete income successfully when owned by user")
    void testDeleteIncome_Success() {
        when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        when(incomeRepository.findById(200L)).thenReturn(Optional.of(income1));

        incomeService.deleteIncome(200L, user1.getEmail());

        verify(incomeRepository, times(1)).delete(income1);
    }

    @Test
    @DisplayName("Should throw UnauthorizedAccessException when deleting another user's income")
    void testDeleteIncome_Unauthorized() {
        when(userRepository.findByEmail(user2.getEmail())).thenReturn(Optional.of(user2));
        when(incomeRepository.findById(200L)).thenReturn(Optional.of(income1));

        assertThrows(UnauthorizedAccessException.class, () ->
                incomeService.deleteIncome(200L, user2.getEmail())
        );

        verify(incomeRepository, never()).delete(any(Income.class));
    }
}
