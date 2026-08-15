package com.slt.expense_tracker.repository;

import com.slt.expense_tracker.entity.Expense;
import com.slt.expense_tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findTop10ByUserOrderByTransactionDateDesc(User user);

    List<Expense> findByUserOrderByTransactionDateDesc(User user);

    Optional<Expense> findByIdAndUser(Long id, User user);
}