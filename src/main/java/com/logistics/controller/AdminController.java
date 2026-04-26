package com.logistics.controller;
import com.logistics.dto.request.AssignAgentRequest;
import com.logistics.dto.response.ApiResponse;
import com.logistics.dto.response.DeliveryResponse;
import com.logistics.dto.response.PagedResponse;
import com.logistics.dto.response.UserResponse;
import com.logistics.enums.UserRole;
import com.logistics.service.DeliveryService;
import com.logistics.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
//Admin Only API endpoints
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin-only user management and assignment APIs")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {
    private final UserService     userService;
    private final DeliveryService deliveryService;
    public AdminController(UserService userService, DeliveryService deliveryService) {
        this.userService     = userService;
        this.deliveryService = deliveryService;
    }
// User Management 
    @GetMapping("/users")
    @Operation(summary = "List all users (paginated), filter by role")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> listUsers(
            @RequestParam(required = false) UserRole role,
            @RequestParam(defaultValue = "0")   int page,
            @RequestParam(defaultValue = "20")  int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), sort);
        Page<UserResponse> data = userService.getAllUsers(role, pageable);
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(data)));
    }
    @GetMapping("/users/{id}")
    @Operation(summary = "Get a user by their ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id)));
    }
    @GetMapping("/users/search")
    @Operation(summary = "Search users by name or email keyword")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        Page<UserResponse> data = userService.searchUsers(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(data)));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete a user by ID")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully"));
    }

    // ─── Delivery Assignment ──────────────────────────────────────────────────

    /**
     * POST /api/admin/shipments/{shipmentId}/assign
     *
     * Assigns an available DeliveryAgent to a Shipment, creating a Delivery record.
     *
     * Request body:
     * { "agentId": 5 }
     */
    @PostMapping("/shipments/{shipmentId}/assign")
    @Operation(summary = "Assign a delivery agent to a shipment")
    public ResponseEntity<ApiResponse<DeliveryResponse>> assignAgent(
            @PathVariable Long shipmentId,
            @Valid @RequestBody AssignAgentRequest request) {

        DeliveryResponse delivery = deliveryService.assignAgent(shipmentId, request);
        return ResponseEntity.ok(
                ApiResponse.success(delivery, "Agent assigned successfully"));
    }
}
