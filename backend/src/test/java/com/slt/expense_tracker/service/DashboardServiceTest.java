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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    @DisplayName("Should correctly compute dashboard totals, balance, category breakdown, and monthly stats")
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

        LocalDate now = LocalDate.now();
        LocalDate startDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        when(incomeRepository.sumAmountByUserAndIncomeDateBetween(user, startDate, endDate)).thenReturn(new BigDecimal("50000.00"));
        when(expenseRepository.sumAmountByUserAndTransactionDateBetween(user, startDate, endDate)).thenReturn(new BigDecimal("15000.00"));
        when(expenseRepository.findCategoryBreakdownByUserAndTransactionDateBetween(user, startDate, endDate)).thenReturn(categoryBreakdownData);

        DashboardResponse dashboard = dashboardService.getDashboardData(user.getEmail());

        assertNotNull(dashboard);
        assertEquals(new BigDecimal("100000.00"), dashboard.getTotalIncome());
        assertEquals(new BigDecimal("45000.00"), dashboard.getTotalExpenses());
        assertEquals(new BigDecimal("55000.00"), dashboard.getBalance());
        assertEquals(new BigDecimal("50000.00"), dashboard.getMonthlyIncomeTotal());
        assertEquals(new BigDecimal("15000.00"), dashboard.getMonthlyExpenseTotal());
        assertEquals("food", dashboard.getHighestExpenseCategory());
        assertEquals(now.getYear(), dashboard.getSelectedYear());
        assertEquals(now.getMonthValue(), dashboard.getSelectedMonth());

        assertEquals(2, dashboard.getRecentTransactions().size());
        assertEquals(1, dashboard.getCategoryBreakdown().size());

        CategorySummaryResponse foodSummary = dashboard.getCategoryBreakdown().get(0);
        assertEquals(ExpenseCategory.FOOD, foodSummary.getCategory());
        assertEquals(new BigDecimal("15000.00"), foodSummary.getTotalAmount());
        assertEquals(33.33, foodSummary.getPercentage());
    }

    @Test
    @DisplayName("Should correctly compute dashboard figures for explicit year and month")
    void testGetDashboardData_WithCustomYearAndMonth() {
        int year = 2026;
        int month = 8;
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = LocalDate.of(year, month, 31);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(incomeRepository.sumAmountByUser(user)).thenReturn(new BigDecimal("200000.00"));
        when(expenseRepository.sumAmountByUser(user)).thenReturn(new BigDecimal("90000.00"));

        when(incomeRepository.sumAmountByUserAndIncomeDateBetween(user, startDate, endDate)).thenReturn(new BigDecimal("80000.00"));
        when(expenseRepository.sumAmountByUserAndTransactionDateBetween(user, startDate, endDate)).thenReturn(new BigDecimal("30000.00"));

        List<Object[]> monthlyBreakdown = new ArrayList<>();
        monthlyBreakdown.add(new Object[]{ExpenseCategory.BILLS, new BigDecimal("20000.00")});
        when(expenseRepository.findCategoryBreakdownByUserAndTransactionDateBetween(user, startDate, endDate)).thenReturn(monthlyBreakdown);

        when(expenseRepository.findTop5ByUserOrderByTransactionDateDescIdDesc(user)).thenReturn(List.of());
        when(incomeRepository.findTop5ByUserOrderByIncomeDateDescIdDesc(user)).thenReturn(List.of());
        when(expenseRepository.findCategoryBreakdownByUser(user)).thenReturn(List.of());

        DashboardResponse dashboard = dashboardService.getDashboardData(user.getEmail(), year, month);

        assertNotNull(dashboard);
        assertEquals(new BigDecimal("80000.00"), dashboard.getMonthlyIncomeTotal());
        assertEquals(new BigDecimal("30000.00"), dashboard.getMonthlyExpenseTotal());
        assertEquals("bills", dashboard.getHighestExpenseCategory());
        assertEquals(2026, dashboard.getSelectedYear());
        assertEquals(8, dashboard.getSelectedMonth());
    }
}
