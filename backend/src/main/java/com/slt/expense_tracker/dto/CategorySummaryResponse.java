package com.slt.expense_tracker.dto;

import com.slt.expense_tracker.entity.ExpenseCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategorySummaryResponse {

    private ExpenseCategory category;
    private BigDecimal totalAmount;
    private Double percentage;
}
