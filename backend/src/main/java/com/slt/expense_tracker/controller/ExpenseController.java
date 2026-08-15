package com.slt.expense_tracker.controller;

import com.slt.expense_tracker.dto.ExpenseRequest;
import com.slt.expense_tracker.dto.ExpenseResponse;
import com.slt.expense_tracker.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

        ExpenseResponse response =
                expenseService.createExpense(
                        request,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getAllExpenses(
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                expenseService.getAllExpenses(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/latest")
    public ResponseEntity<List<ExpenseResponse>> getLatestExpenses(
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                expenseService.getLatestExpenses(
                        authentication.getName()
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequest request,
            Authentication authentication
    ) {

        ExpenseResponse response =
                expenseService.updateExpense(
                        id,
                        request,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(
            @PathVariable Long id,
            Authentication authentication
    ) {

        expenseService.deleteExpense(
                id,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }
}