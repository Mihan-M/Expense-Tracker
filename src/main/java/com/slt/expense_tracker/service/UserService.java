package com.slt.expense_tracker.service;

import com.slt.expense_tracker.dto.UserResponse;
import com.slt.expense_tracker.entity.User;
import com.slt.expense_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAddress()
        );
    }
}