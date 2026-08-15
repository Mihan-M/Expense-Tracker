package com.slt.expense_tracker.service;

import com.slt.expense_tracker.dto.IncomeRequest;
import com.slt.expense_tracker.dto.IncomeResponse;
import com.slt.expense_tracker.entity.Income;
import com.slt.expense_tracker.entity.User;
import com.slt.expense_tracker.exception.ResourceNotFoundException;
import com.slt.expense_tracker.exception.UnauthorizedAccessException;
import com.slt.expense_tracker.repository.IncomeRepository;
import com.slt.expense_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;

    @Transactional
    public IncomeResponse createIncome(IncomeRequest request, String userEmail) {
        User user = getUserByEmail(userEmail);

        Income income = Income.builder()
                .title(request.getTitle())
                .amount(request.getAmount())
                .incomeDate(request.getIncomeDate())
                .note(request.getNote())
                .user(user)
                .build();

        Income saved = incomeRepository.save(income);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<IncomeResponse> getIncomes(String userEmail) {
        User user = getUserByEmail(userEmail);
        Sort sort = Sort.by(Sort.Direction.DESC, "incomeDate", "id");

        return incomeRepository.findByUser(user, sort)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IncomeResponse> getLatestIncomes(String userEmail) {
        User user = getUserByEmail(userEmail);

        return incomeRepository.findTop5ByUserOrderByIncomeDateDescIdDesc(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public IncomeResponse updateIncome(Long id, IncomeRequest request, String userEmail) {
        User user = getUserByEmail(userEmail);
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Income not found with id: " + id));

        verifyOwnership(income, user);

        income.setTitle(request.getTitle());
        income.setAmount(request.getAmount());
        income.setIncomeDate(request.getIncomeDate());
        income.setNote(request.getNote());

        Income updated = incomeRepository.save(income);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteIncome(Long id, String userEmail) {
        User user = getUserByEmail(userEmail);
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Income not found with id: " + id));

        verifyOwnership(income, user);

        incomeRepository.delete(income);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private void verifyOwnership(Income income, User user) {
        if (!income.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You do not have permission to access or modify this income record");
        }
    }

    private IncomeResponse mapToResponse(Income income) {
        return IncomeResponse.builder()
                .id(income.getId())
                .title(income.getTitle())
                .amount(income.getAmount())
                .incomeDate(income.getIncomeDate())
                .note(income.getNote())
                .userId(income.getUser().getId())
                .createdAt(income.getCreatedAt())
                .updatedAt(income.getUpdatedAt())
                .build();
    }
}
