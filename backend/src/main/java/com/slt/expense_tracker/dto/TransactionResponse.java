package com.slt.expense_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {

    private Long id;
    private String type; // "EXPENSE" or "INCOME"
    private String title;
    private BigDecimal amount;
    private LocalDate date;
    private String category; // Expense category name or null for income
    private String note;
}
