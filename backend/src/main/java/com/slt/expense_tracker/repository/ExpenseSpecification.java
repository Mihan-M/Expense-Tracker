package com.slt.expense_tracker.repository;

import com.slt.expense_tracker.entity.Expense;
import com.slt.expense_tracker.entity.ExpenseCategory;
import com.slt.expense_tracker.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseSpecification {

    public static Specification<Expense> filterExpenses(
            User user,
            ExpenseCategory category,
            LocalDate startDate,
            LocalDate endDate,
            String search
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // User isolation is mandatory
            predicates.add(cb.equal(root.get("user"), user));

            if (category != null) {
                predicates.add(cb.equal(root.get("category"), category));
            }

            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("transactionDate"), startDate));
            }

            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("transactionDate"), endDate));
            }

            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("title")), searchPattern));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
