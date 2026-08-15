package com.slt.expense_tracker.dto;

import com.slt.expense_tracker.entity.ExpenseCategory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should accept valid password matching all criteria")
    void testRegisterRequest_ValidPassword() {
        RegisterRequest request = new RegisterRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setAddress("123 Main St");
        request.setPassword("SecurePass@123");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should reject password shorter than 8 characters")
    void testRegisterRequest_ShortPassword() {
        RegisterRequest request = new RegisterRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setAddress("123 Main St");
        request.setPassword("Pass@1");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    @DisplayName("Should reject password lacking special characters")
    void testRegisterRequest_NoSpecialCharPassword() {
        RegisterRequest request = new RegisterRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setAddress("123 Main St");
        request.setPassword("Password123");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should reject past transaction date in ExpenseRequest")
    void testExpenseRequest_PastDate() {
        ExpenseRequest request = ExpenseRequest.builder()
                .title("Dinner")
                .category(ExpenseCategory.FOOD)
                .amount(new BigDecimal("50.00"))
                .transactionDate(LocalDate.now().minusDays(1))
                .build();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("transactionDate")));
    }

    @Test
    @DisplayName("Should accept present and future transaction dates in ExpenseRequest")
    void testExpenseRequest_ValidDate() {
        ExpenseRequest request = ExpenseRequest.builder()
                .title("Dinner")
                .category(ExpenseCategory.FOOD)
                .amount(new BigDecimal("50.00"))
                .transactionDate(LocalDate.now().plusDays(5))
                .build();

        Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should reject past income date in IncomeRequest")
    void testIncomeRequest_PastDate() {
        IncomeRequest request = IncomeRequest.builder()
                .title("Salary")
                .amount(new BigDecimal("5000.00"))
                .incomeDate(LocalDate.now().minusDays(5))
                .build();

        Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("incomeDate")));
    }

}
