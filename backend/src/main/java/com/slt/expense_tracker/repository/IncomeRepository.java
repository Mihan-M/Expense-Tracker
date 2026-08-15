package com.slt.expense_tracker.repository;

import com.slt.expense_tracker.entity.Income;
import com.slt.expense_tracker.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {

    List<Income> findByUser(User user, Sort sort);

    List<Income> findTop5ByUserOrderByIncomeDateDescIdDesc(User user);

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i WHERE i.user = :user")
    BigDecimal sumAmountByUser(@Param("user") User user);

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i WHERE i.user = :user AND i.incomeDate >= :startDate AND i.incomeDate <= :endDate")
    BigDecimal sumAmountByUserAndIncomeDateBetween(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
