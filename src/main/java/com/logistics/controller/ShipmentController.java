package com.logistics.controller;

import com.logistics.dto.request.AddTrackingInfoRequest;
import com.logistics.dto.response.ApiResponse;
import com.logistics.dto.response.PagedResponse;
import com.logistics.dto.response.ShipmentResponse;
import com.logistics.enums.ShipmentStatus;
import com.logistics.security.UserPrincipal;
import com.logistics.service.ShipmentService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoints for Shipment management.
 *
 * Endpoint summary:
 * ┌────────────────────────────────────────────┬──────────────────────────────┐
 * │ Method + Path                              │ Role(s)                      │
 * ├────────────────────────────────────────────┼──────────────────────────────┤
 * │ GET  /api/shipments                        │ ADMIN                        │
 * │ GET  /api/shipments/my                     │ CUSTOMER                     │
 * │ GET  /api/shipments/{id}                   │ CUSTOMER (own), ADMIN, AGENT │
 * │ GET  /api/shipments/order/{orderId}        │ CUSTOMER (own), ADMIN        │
 * │ PATCH /api/shipments/{id}/status           │ ADMIN                        │
 * │ POST  /api/shipments/{id}/tracking         │ ADMIN, DELIVERY_AGENT        │
 * └────────────────────────────────────────────┴──────────────────────────────┘
 */
@RestController
@RequestMapping("/api/shipments")
@Tag(name = "Shipments", description = "Shipment tracking and management APIs")
@SecurityRequirement(name = "bearerAuth")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    // ─── Admin: list all shipments ────────────────────────────────────────────

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: list all shipments with optional status filter")
    public ResponseEntity<ApiResponse<PagedResponse<ShipmentResponse>>> getAllShipments(
            @RequestParam(required = false) ShipmentStatus status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Pageable pageable = pageOf(page, size, sortBy, sortDir);
        Page<ShipmentResponse> data = shipmentService.getAllShipments(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(data)));
    }

    // ─── Customer: view own shipments ─────────────────────────────────────────

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Customer: view all their shipments (across orders)")
    public ResponseEntity<ApiResponse<PagedResponse<ShipmentResponse>>> getMyShipments(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        Page<ShipmentResponse> data =
                shipmentService.getShipmentsByCustomer(principal.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(data)));
    }

    // ─── Get shipment by ID ───────────────────────────────────────────────────

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN','DELIVERY_AGENT')")
    @Operation(summary = "Get shipment details (with packages and tracking history)")
    public ResponseEntity<ApiResponse<ShipmentResponse>> getShipmentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(ApiResponse.success(shipmentService.getShipmentById(id)));
    }

    // ─── Get shipments by order ───────────────────────────────────────────────

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    @Operation(summary = "List all shipments belonging to a specific order")
    public ResponseEntity<ApiResponse<List<ShipmentResponse>>> getByOrder(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                ApiResponse.success(shipmentService.getShipmentsByOrder(orderId)));
    }

    // ─── Admin: update shipment status ───────────────────────────────────────

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: advance shipment status along the lifecycle")
    public ResponseEntity<ApiResponse<ShipmentResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        ShipmentResponse updated = shipmentService.updateShipmentStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(updated, "Shipment status updated"));
    }

    // ─── Admin / Agent: add tracking event ───────────────────────────────────

    @PostMapping("/{id}/tracking")
    @PreAuthorize("hasAnyRole('ADMIN','DELIVERY_AGENT')")
    @Operation(summary = "Append a tracking event to the shipment timeline")
    public ResponseEntity<ApiResponse<ShipmentResponse>> addTracking(
            @PathVariable Long id,
            @Valid @RequestBody AddTrackingInfoRequest request) {

        ShipmentResponse updated = shipmentService.addTrackingInfo(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated, "Tracking info added"));
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    private Pageable pageOf(int page, int size, String sortBy, String dir) {
        Sort sort = dir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(page, Math.min(size, 100), sort);
    }
}
