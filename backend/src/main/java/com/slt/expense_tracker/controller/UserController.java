package com.slt.expense_tracker.controller;

import com.slt.expense_tracker.dto.UserResponse;
import com.slt.expense_tracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(
            Authentication authentication
    ) {

        String email = authentication.getName();

        UserResponse response =
                userService.getProfile(email);

        return ResponseEntity.ok(response);
    }
}