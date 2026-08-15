package com.slt.expense_tracker.service;

import com.slt.expense_tracker.dto.ExpenseRequest;
import com.slt.expense_tracker.dto.ExpenseResponse;
import com.slt.expense_tracker.entity.Expense;
import com.slt.expense_tracker.entity.User;
import com.slt.expense_tracker.repository.ExpenseRepository;
import com.slt.expense_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseResponse createExpense(
            ExpenseRequest request,
            String email
    ) {

        User user = getUser(email);

        Expense expense = new Expense();

        expense.setTitle(request.getTitle());
        expense.setCategory(request.getCategory());
        expense.setAmount(request.getAmount());
        expense.setTransactionDate(request.getTransactionDate());
        expense.setNote(request.getNote());
        expense.setUser(user);

        Expense savedExpense = expenseRepository.save(expense);

        return toResponse(savedExpense);
    }

    public List<ExpenseResponse> getAllExpenses(String email) {

        User user = getUser(email);

        return expenseRepository
                .findByUserOrderByTransactionDateDesc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ExpenseResponse> getLatestExpenses(String email) {

        User user = getUser(email);

        return expenseRepository
                .findTop10ByUserOrderByTransactionDateDesc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ExpenseResponse updateExpense(
            Long id,
            ExpenseRequest request,
            String email
    ) {

        User user = getUser(email);

        Expense expense = expenseRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new RuntimeException("Expense not found")
                );

        expense.setTitle(request.getTitle());
        expense.setCategory(request.getCategory());
        expense.setAmount(request.getAmount());
        expense.setTransactionDate(request.getTransactionDate());
        expense.setNote(request.getNote());

        Expense updatedExpense = expenseRepository.save(expense);

        return toResponse(updatedExpense);
    }

    public void deleteExpense(
            Long id,
            String email
    ) {

        User user = getUser(email);

        Expense expense = expenseRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new RuntimeException("Expense not found")
                );

        expenseRepository.delete(expense);
    }

    private User getUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );
    }

    private ExpenseResponse toResponse(Expense expense) {

        return new ExpenseResponse(
                expense.getId(),
                expense.getTitle(),
                expense.getCategory(),
                expense.getAmount(),
                expense.getTransactionDate(),
                expense.getNote()
        );
    }
}