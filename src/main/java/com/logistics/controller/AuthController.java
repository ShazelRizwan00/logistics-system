package com.logistics.controller;

import com.logistics.dto.request.LoginRequest;
import com.logistics.dto.request.RegisterRequest;
import com.logistics.dto.response.ApiResponse;
import com.logistics.dto.response.AuthResponse;
import com.logistics.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Handles user registration and login.
 * These endpoints are PUBLIC — no JWT required (see SecurityConfig).
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Register and login endpoints")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/auth/register
     *
     * Request body example:
     * {
     *   "name": "Alice Smith",
     *   "contactInfo": "alice@example.com",
     *   "password": "secret123",
     *   "role": "CUSTOMER",
     *   "address": "123 Main St, Lahore"
     * }
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user (CUSTOMER / DELIVERY_AGENT / ADMIN)")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        AuthResponse response = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "User registered successfully"));
    }

    /**
     * POST /api/auth/login
     *
     * Request body example:
     * {
     *   "contactInfo": "alice@example.com",
     *   "password": "secret123"
     * }
     *
     * Returns a JWT that must be sent in the Authorization header as:
     *   Authorization: Bearer <token>
     */
    @PostMapping("/login")
    @Operation(summary = "Login and receive a JWT token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }
}
