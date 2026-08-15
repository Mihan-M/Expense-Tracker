package com.slt.expense_tracker.service;

import com.slt.expense_tracker.dto.CategorySummaryResponse;
import com.slt.expense_tracker.dto.DashboardResponse;
import com.slt.expense_tracker.dto.TransactionResponse;
import com.slt.expense_tracker.entity.Expense;
import com.slt.expense_tracker.entity.ExpenseCategory;
import com.slt.expense_tracker.entity.Income;
import com.slt.expense_tracker.entity.User;
import com.slt.expense_tracker.exception.ResourceNotFoundException;
import com.slt.expense_tracker.repository.ExpenseRepository;
import com.slt.expense_tracker.repository.IncomeRepository;
import com.slt.expense_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboardData(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        BigDecimal totalIncome = incomeRepository.sumAmountByUser(user);
        if (totalIncome == null) {
            totalIncome = BigDecimal.ZERO;
        }

        BigDecimal totalExpenses = expenseRepository.sumAmountByUser(user);
        if (totalExpenses == null) {
            totalExpenses = BigDecimal.ZERO;
        }

        BigDecimal balance = totalIncome.subtract(totalExpenses);

        List<TransactionResponse> recentTransactions = getRecentTransactions(user);
        List<CategorySummaryResponse> categoryBreakdown = getCategoryBreakdown(user, totalExpenses);

        return DashboardResponse.builder()
                .totalIncome(totalIncome.setScale(2, RoundingMode.HALF_UP))
                .totalExpenses(totalExpenses.setScale(2, RoundingMode.HALF_UP))
                .balance(balance.setScale(2, RoundingMode.HALF_UP))
                .recentTransactions(recentTransactions)
                .categoryBreakdown(categoryBreakdown)
                .build();
    }

    private List<TransactionResponse> getRecentTransactions(User user) {
        List<Expense> latestExpenses = expenseRepository.findTop5ByUserOrderByTransactionDateDescIdDesc(user);
        List<Income> latestIncomes = incomeRepository.findTop5ByUserOrderByIncomeDateDescIdDesc(user);

        List<TransactionResponse> transactions = new ArrayList<>();

        for (Expense expense : latestExpenses) {
            transactions.add(TransactionResponse.builder()
                    .id(expense.getId())
                    .type("EXPENSE")
                    .title(expense.getTitle())
                    .amount(expense.getAmount())
                    .date(expense.getTransactionDate())
                    .category(expense.getCategory() != null ? expense.getCategory().getValue() : null)
                    .note(expense.getNote())
                    .build());
        }

        for (Income income : latestIncomes) {
            transactions.add(TransactionResponse.builder()
                    .id(income.getId())
                    .type("INCOME")
                    .title(income.getTitle())
                    .amount(income.getAmount())
                    .date(income.getIncomeDate())
                    .category(null)
                    .note(income.getNote())
                    .build());
        }

        return transactions.stream()
                .sorted(Comparator.comparing(TransactionResponse::getDate).reversed()
                        .thenComparing(TransactionResponse::getId, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(10)
                .collect(Collectors.toList());
    }

    private List<CategorySummaryResponse> getCategoryBreakdown(User user, BigDecimal totalExpenses) {
        List<Object[]> rawBreakdown = expenseRepository.findCategoryBreakdownByUser(user);
        List<CategorySummaryResponse> breakdown = new ArrayList<>();

        for (Object[] row : rawBreakdown) {
            ExpenseCategory category = (ExpenseCategory) row[0];
            BigDecimal sum = (BigDecimal) row[1];

            double percentage = 0.0;
            if (totalExpenses.compareTo(BigDecimal.ZERO) > 0 && sum != null) {
                percentage = sum.multiply(new BigDecimal("100"))
                        .divide(totalExpenses, 2, RoundingMode.HALF_UP)
                        .doubleValue();
            }

            breakdown.add(CategorySummaryResponse.builder()
                    .category(category)
                    .totalAmount(sum != null ? sum.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO)
                    .percentage(percentage)
                    .build());
        }

        return breakdown;
    }
}
