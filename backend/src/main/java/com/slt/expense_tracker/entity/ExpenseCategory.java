package com.slt.expense_tracker.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ExpenseCategory {
    FOOD("food"),
    TRANSPORT("transport"),
    BILLS("bills"),
    SHOPPING("shopping"),
    ENTERTAINMENT("entertainment"),
    OTHER("other");

    private final String value;

    ExpenseCategory(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ExpenseCategory fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return OTHER;
        }
        String normalized = value.trim().toLowerCase();
        for (ExpenseCategory category : ExpenseCategory.values()) {
            if (category.value.equalsIgnoreCase(normalized) || category.name().equalsIgnoreCase(normalized)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown category: " + value + ". Supported categories are: food, transport, bills, shopping, entertainment, other.");
    }
}
