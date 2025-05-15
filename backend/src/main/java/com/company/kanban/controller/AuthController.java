package com.company.kanban.controller;

import com.company.kanban.model.entity.User;
import com.company.kanban.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Tag(name = "Authentication", description = "User login and registration")
public class AuthController {

    private final UserService userService;

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody @Valid User user) {
        User savedUser = userService.saveUser(user);
        savedUser.setPassword(null);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @Operation(summary = "Login a user and receive JWT (handled by filter)")
    @PostMapping("/login")
    public void login(@RequestBody User loginUser) {
        // auth handled by AuthenticationFilter, not here.
        throw new UnsupportedOperationException("Handled by AuthenticationFilter");
    }
}
