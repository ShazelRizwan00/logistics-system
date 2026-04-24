package com.logistics.controller;

import com.logistics.dto.response.ApiResponse;
import com.logistics.dto.response.UserResponse;
import com.logistics.security.UserPrincipal;
import com.logistics.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * User profile endpoints available to any authenticated user.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User profile endpoints")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET /api/users/me
     * Returns the profile of the currently authenticated user.
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user's profile")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(
            @AuthenticationPrincipal UserPrincipal principal) {

        UserResponse profile = userService.getCurrentUser(principal.getUsername());
        return ResponseEntity.ok(ApiResponse.success(profile));
    }
}
