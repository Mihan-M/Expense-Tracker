package com.slt.expense_tracker.service;

import com.slt.expense_tracker.dto.CategorySummaryResponse;
import com.slt.expense_tracker.dto.DashboardResponse;
import com.slt.expense_tracker.entity.Expense;
import com.slt.expense_tracker.entity.ExpenseCategory;
import com.slt.expense_tracker.entity.Income;
import com.slt.expense_tracker.entity.User;
import com.slt.expense_tracker.repository.ExpenseRepository;
import com.slt.expense_tracker.repository.IncomeRepository;
import com.slt.expense_tracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DashboardService dashboardService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("user@example.com").name("Dashboard User").build();
    }

    @Test
    @DisplayName("Should correctly compute dashboard totals, balance, and category breakdown")
    void testGetDashboardData_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(incomeRepository.sumAmountByUser(user)).thenReturn(new BigDecimal("100000.00"));
        when(expenseRepository.sumAmountByUser(user)).thenReturn(new BigDecimal("45000.00"));

        Expense expense = Expense.builder()
                .id(1L)
                .title("Groceries")
                .amount(new BigDecimal("15000.00"))
                .category(ExpenseCategory.FOOD)
                .transactionDate(LocalDate.now())
                .user(user)
                .build();

        Income income = Income.builder()
                .id(1L)
                .title("Salary")
                .amount(new BigDecimal("100000.00"))
                .incomeDate(LocalDate.now())
                .user(user)
                .build();

        when(expenseRepository.findTop5ByUserOrderByTransactionDateDescIdDesc(user)).thenReturn(List.of(expense));
        when(incomeRepository.findTop5ByUserOrderByIncomeDateDescIdDesc(user)).thenReturn(List.of(income));

        List<Object[]> categoryBreakdownData = new ArrayList<>();
        categoryBreakdownData.add(new Object[]{ExpenseCategory.FOOD, new BigDecimal("15000.00")});
        when(expenseRepository.findCategoryBreakdownByUser(user)).thenReturn(categoryBreakdownData);

        DashboardResponse dashboard = dashboardService.getDashboardData(user.getEmail());

        assertNotNull(dashboard);
        assertEquals(new BigDecimal("100000.00"), dashboard.getTotalIncome());
        assertEquals(new BigDecimal("45000.00"), dashboard.getTotalExpenses());
        assertEquals(new BigDecimal("55000.00"), dashboard.getBalance());

        assertEquals(2, dashboard.getRecentTransactions().size());
        assertEquals(1, dashboard.getCategoryBreakdown().size());

        CategorySummaryResponse foodSummary = dashboard.getCategoryBreakdown().get(0);
        assertEquals(ExpenseCategory.FOOD, foodSummary.getCategory());
        assertEquals(new BigDecimal("15000.00"), foodSummary.getTotalAmount());
        assertEquals(33.33, foodSummary.getPercentage());
    }
}
