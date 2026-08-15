package com.slt.expense_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {

    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal balance;
    private BigDecimal monthlyExpenseTotal;
    private BigDecimal monthlyIncomeTotal;
    private String highestExpenseCategory;
    private Integer selectedYear;
    private Integer selectedMonth;
    private List<TransactionResponse> recentTransactions;
    private List<CategorySummaryResponse> categoryBreakdown;
}
