package com.slt.expense_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncomeResponse {

    private Long id;
    private String title;
    private BigDecimal amount;
    private LocalDate incomeDate;
    private String note;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
