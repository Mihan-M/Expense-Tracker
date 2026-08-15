package com.slt.expense_tracker.controller;

import com.slt.expense_tracker.dto.ExpenseRequest;
import com.slt.expense_tracker.dto.ExpenseResponse;
import com.slt.expense_tracker.entity.ExpenseCategory;
import com.slt.expense_tracker.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(
            @Valid @RequestBody ExpenseRequest request,
            Authentication authentication
    ) {
        ExpenseResponse response = expenseService.createExpense(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getExpenses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String search,
            Authentication authentication
    ) {
        ExpenseCategory expenseCategory = null;
        if (category != null && !category.trim().isEmpty()) {
            expenseCategory = ExpenseCategory.fromValue(category);
        }

        List<ExpenseResponse> responses = expenseService.getExpenses(
                authentication.getName(), expenseCategory, startDate, endDate, search
        );
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/latest")
    public ResponseEntity<List<ExpenseResponse>> getLatestExpenses(
            Authentication authentication
    ) {
        List<ExpenseResponse> responses = expenseService.getLatestExpenses(authentication.getName());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequest request,
            Authentication authentication
    ) {
        ExpenseResponse response = expenseService.updateExpense(id, request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(
            @PathVariable Long id,
            Authentication authentication
    ) {
        expenseService.deleteExpense(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}