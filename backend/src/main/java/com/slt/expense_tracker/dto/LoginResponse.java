package com.slt.expense_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private String message;
    private String token;
    private Long userId;
    private String name;
    private String email;
    private String role;
}