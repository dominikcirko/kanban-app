package com.company.kanban.unit.service;

import com.company.kanban.model.entity.User;
import com.company.kanban.repository.UserRepository;
import com.company.kanban.service.implementations.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import jakarta.persistence.EntityNotFoundException;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "testuser", "password");
    }

    @Test
    void getUserById_WhenUserExists_ReturnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.getUser(1L);

        assertEquals("testuser", result.getUsername());
    }

    @Test
    void getUserById_WhenUserNotExists_ThrowsException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUser(999L));
    }

    @Test
    void getUserByUsername_WhenUserExists_ReturnsUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        User result = userService.getUser("testuser");

        assertEquals(1L, result.getId());
    }

    @Test
    void getUserByUsername_WhenUserNotExists_ThrowsException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUser("unknown"));
    }

    @Test
    void saveUser_EncodesPasswordAndSaves() {
        User newUser = new User(null, "newuser", "rawpass");
        when(passwordEncoder.encode("rawpass")).thenReturn("encodedpass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            return new User(2L, u.getUsername(), u.getPassword());
        });

        User savedUser = userService.saveUser(newUser);

        assertEquals("encodedpass", savedUser.getPassword());
        assertEquals("newuser", savedUser.getUsername());
        verify(passwordEncoder).encode("rawpass");
    }

    @Test
    void deleteUser_RemovesExistingUser() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void unwrapUser_WhenPresent_ReturnsUser() {
        Optional<User> optionalUser = Optional.of(testUser);

        User result = UserServiceImpl.unwrapUser(optionalUser);

        assertEquals(testUser, result);
    }

    @Test
    void unwrapUser_WhenEmpty_ThrowsException() {
        Optional<User> optionalUser = Optional.empty();

        assertThrows(EntityNotFoundException.class, () ->
                UserServiceImpl.unwrapUser(optionalUser));
    }

    @Test
    void saveUser_WithExistingId_UpdatesPassword() {
        User updated = new User(1L, "existing", "newpass");

        when(passwordEncoder.encode("newpass")).thenReturn("encodednew");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.saveUser(updated);

        assertEquals("encodednew", result.getPassword());
        verify(passwordEncoder).encode("newpass");
    }


}