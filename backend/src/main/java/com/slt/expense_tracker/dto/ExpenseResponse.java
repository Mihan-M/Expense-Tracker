package com.slt.expense_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ExpenseResponse {

    private Long id;
    private String title;
    private String category;
    private BigDecimal amount;
    private LocalDate transactionDate;
    private String note;
}