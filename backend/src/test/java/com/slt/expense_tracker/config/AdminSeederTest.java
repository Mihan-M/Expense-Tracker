package com.slt.expense_tracker.config;

import com.slt.expense_tracker.entity.Role;
import com.slt.expense_tracker.entity.User;
import com.slt.expense_tracker.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminSeederTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @InjectMocks
    private AdminSeeder adminSeeder;


    @Test
    @DisplayName("Should seed admin user when email does not exist")
    void testRun_SeedsAdminWhenNotPresent() {
        String email = "admin@example.com";
        String password = "Admin@12345";

        ReflectionTestUtils.setField(adminSeeder, "adminEmail", email);
        ReflectionTestUtils.setField(adminSeeder, "adminPassword", password);

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("hashedAdminPass");

        adminSeeder.run();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());

        User createdAdmin = userCaptor.getValue();
        assertNotNull(createdAdmin);
        assertEquals(email, createdAdmin.getEmail());
        assertEquals("hashedAdminPass", createdAdmin.getPassword());
        assertEquals(Role.ADMIN, createdAdmin.getRole());
    }

    @Test
    @DisplayName("Should not seed admin user when email already exists")
    void testRun_DoesNotSeedWhenPresent() {
        String email = "admin@example.com";
        ReflectionTestUtils.setField(adminSeeder, "adminEmail", email);

        when(userRepository.existsByEmail(email)).thenReturn(true);

        adminSeeder.run();

        verify(userRepository, never()).save(any(User.class));
    }
}
