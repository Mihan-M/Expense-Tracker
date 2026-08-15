package com.slt.expense_tracker.service;

import com.slt.expense_tracker.dto.ExpenseRequest;
import com.slt.expense_tracker.dto.ExpenseResponse;
import com.slt.expense_tracker.entity.Expense;
import com.slt.expense_tracker.entity.ExpenseCategory;
import com.slt.expense_tracker.entity.User;
import com.slt.expense_tracker.exception.ResourceNotFoundException;
import com.slt.expense_tracker.exception.UnauthorizedAccessException;
import com.slt.expense_tracker.repository.ExpenseRepository;
import com.slt.expense_tracker.repository.ExpenseSpecification;
import com.slt.expense_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    @Transactional
    public ExpenseResponse createExpense(ExpenseRequest request, String userEmail) {
        User user = getUserByEmail(userEmail);

        Expense expense = Expense.builder()
                .title(request.getTitle())
                .category(request.getCategory())
                .amount(request.getAmount())
                .transactionDate(request.getTransactionDate())
                .note(request.getNote())
                .user(user)
                .build();

        Expense saved = expenseRepository.save(expense);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> getExpenses(
            String userEmail,
            ExpenseCategory category,
            LocalDate startDate,
            LocalDate endDate,
            String search
    ) {
        User user = getUserByEmail(userEmail);

        Specification<Expense> spec = ExpenseSpecification.filterExpenses(
                user, category, startDate, endDate, search
        );

        Sort sort = Sort.by(Sort.Direction.DESC, "transactionDate", "id");

        return expenseRepository.findAll(spec, sort)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> getLatestExpenses(String userEmail) {
        User user = getUserByEmail(userEmail);

        return expenseRepository.findTop5ByUserOrderByTransactionDateDescIdDesc(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ExpenseResponse updateExpense(Long id, ExpenseRequest request, String userEmail) {
        User user = getUserByEmail(userEmail);
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        verifyOwnership(expense, user);

        expense.setTitle(request.getTitle());
        expense.setCategory(request.getCategory());
        expense.setAmount(request.getAmount());
        expense.setTransactionDate(request.getTransactionDate());
        expense.setNote(request.getNote());

        Expense updated = expenseRepository.save(expense);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteExpense(Long id, String userEmail) {
        User user = getUserByEmail(userEmail);
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        verifyOwnership(expense, user);

        expenseRepository.delete(expense);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private void verifyOwnership(Expense expense, User user) {
        if (!expense.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You do not have permission to access or modify this expense");
        }
    }

    private ExpenseResponse mapToResponse(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .title(expense.getTitle())
                .category(expense.getCategory())
                .amount(expense.getAmount())
                .transactionDate(expense.getTransactionDate())
                .note(expense.getNote())
                .userId(expense.getUser().getId())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }
}