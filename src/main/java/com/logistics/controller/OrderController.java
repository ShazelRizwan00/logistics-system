package com.logistics.controller;

import com.logistics.dto.request.CreateOrderRequest;
import com.logistics.dto.request.UpdateOrderStatusRequest;
import com.logistics.dto.response.ApiResponse;
import com.logistics.dto.response.OrderResponse;
import com.logistics.dto.response.PagedResponse;
import com.logistics.enums.OrderStatus;
import com.logistics.security.UserPrincipal;
import com.logistics.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoints for Order management.
 *
 * Endpoint summary:
 * ┌─────────────────────────────────────┬─────────────────────────────────┐
 * │ Method + Path                       │ Role(s)                         │
 * ├─────────────────────────────────────┼─────────────────────────────────┤
 * │ POST   /api/orders                  │ CUSTOMER                        │
 * │ GET    /api/orders                  │ ADMIN                           │
 * │ GET    /api/orders/my               │ CUSTOMER                        │
 * │ GET    /api/orders/{id}             │ CUSTOMER (own), ADMIN           │
 * │ PATCH  /api/orders/{id}/status      │ ADMIN                           │
 * │ DELETE /api/orders/{id}/cancel      │ CUSTOMER (own)                  │
 * └─────────────────────────────────────┴─────────────────────────────────┘
 */
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Order management APIs")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ─── Customer: create order ───────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Create a new order with shipments and payment")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateOrderRequest request) {

        OrderResponse order = orderService.createOrder(request, principal.getUsername());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(order, "Order created successfully"));
    }

    // ─── Customer: view own orders ────────────────────────────────────────────

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get current customer's orders (paginated, optional status filter)")
    public ResponseEntity<ApiResponse<PagedResponse<OrderResponse>>> getMyOrders(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0")   int page,
            @RequestParam(defaultValue = "10")  int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Pageable pageable = buildPageable(page, size, sortBy, sortDir);
        PagedResponse<OrderResponse> data = orderService.getMyOrders(
                principal.getUsername(), status, pageable);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    // ─── Customer / Admin: get single order ───────────────────────────────────

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    @Operation(summary = "Get order details by ID")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        OrderResponse order = orderService.getOrderById(id);

        // Customers can only view their own orders
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !order.getCustomerId().equals(principal.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied to order " + id));
        }

        return ResponseEntity.ok(ApiResponse.success(order));
    }

    // ─── Admin: list all orders ───────────────────────────────────────────────

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: list all orders with optional status filter")
    public ResponseEntity<ApiResponse<PagedResponse<OrderResponse>>> getAllOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0")   int page,
            @RequestParam(defaultValue = "20")  int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Pageable pageable = buildPageable(page, size, sortBy, sortDir);
        PagedResponse<OrderResponse> data = orderService.getAllOrders(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    // ─── Admin: update order status ───────────────────────────────────────────

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: advance order status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {

        OrderResponse updated = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated, "Order status updated"));
    }

    // ─── Customer: cancel order ───────────────────────────────────────────────

    @DeleteMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Customer: cancel a PENDING or CONFIRMED order")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        orderService.cancelOrder(id, principal.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully"));
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    private Pageable buildPageable(int page, int size, String sortBy, String dir) {
        Sort sort = dir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(page, Math.min(size, 100), sort);
    }
}
