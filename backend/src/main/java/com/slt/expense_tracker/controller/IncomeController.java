package com.slt.expense_tracker.controller;

import com.slt.expense_tracker.dto.IncomeRequest;
import com.slt.expense_tracker.dto.IncomeResponse;
import com.slt.expense_tracker.service.IncomeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incomes")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<IncomeResponse> createIncome(
            @Valid @RequestBody IncomeRequest request,
            Authentication authentication
    ) {
        IncomeResponse response = incomeService.createIncome(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<IncomeResponse>> getIncomes(
            Authentication authentication
    ) {
        List<IncomeResponse> responses = incomeService.getIncomes(authentication.getName());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/latest")
    public ResponseEntity<List<IncomeResponse>> getLatestIncomes(
            Authentication authentication
    ) {
        List<IncomeResponse> responses = incomeService.getLatestIncomes(authentication.getName());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncomeResponse> updateIncome(
            @PathVariable Long id,
            @Valid @RequestBody IncomeRequest request,
            Authentication authentication
    ) {
        IncomeResponse response = incomeService.updateIncome(id, request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(
            @PathVariable Long id,
            Authentication authentication
    ) {
        incomeService.deleteIncome(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
