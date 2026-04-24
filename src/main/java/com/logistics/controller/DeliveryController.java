package com.logistics.controller;

import com.logistics.dto.request.UpdateDeliveryStatusRequest;
import com.logistics.dto.response.ApiResponse;
import com.logistics.dto.response.DeliveryResponse;
import com.logistics.dto.response.PagedResponse;
import com.logistics.enums.DeliveryStatus;
import com.logistics.security.UserPrincipal;
import com.logistics.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoints for Delivery management.
 *
 * Endpoint summary:
 * ┌──────────────────────────────────────────┬────────────────────────────┐
 * │ Method + Path                            │ Role(s)                    │
 * ├──────────────────────────────────────────┼────────────────────────────┤
 * │ GET   /api/deliveries/{id}               │ ADMIN, DELIVERY_AGENT      │
 * │ GET   /api/deliveries/shipment/{shipId}  │ ADMIN, DELIVERY_AGENT      │
 * │ GET   /api/deliveries/my                 │ DELIVERY_AGENT             │
 * │ PATCH /api/deliveries/{id}/status        │ DELIVERY_AGENT, ADMIN      │
 * └──────────────────────────────────────────┴────────────────────────────┘
 */
@RestController
@RequestMapping("/api/deliveries")
@Tag(name = "Deliveries", description = "Last-mile delivery management")
@SecurityRequirement(name = "bearerAuth")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    // ─── Get single delivery ──────────────────────────────────────────────────

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DELIVERY_AGENT')")
    @Operation(summary = "Get delivery details by ID")
    public ResponseEntity<ApiResponse<DeliveryResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(deliveryService.getDeliveryById(id)));
    }

    // ─── Get delivery by shipment ─────────────────────────────────────────────

    @GetMapping("/shipment/{shipmentId}")
    @PreAuthorize("hasAnyRole('ADMIN','DELIVERY_AGENT','CUSTOMER')")
    @Operation(summary = "Get the delivery record associated with a shipment")
    public ResponseEntity<ApiResponse<DeliveryResponse>> getByShipment(
            @PathVariable Long shipmentId) {
        return ResponseEntity.ok(
                ApiResponse.success(deliveryService.getDeliveryByShipment(shipmentId)));
    }

    // ─── Agent: view own deliveries ───────────────────────────────────────────

    @GetMapping("/my")
    @PreAuthorize("hasRole('DELIVERY_AGENT')")
    @Operation(summary = "Agent: view own assigned deliveries with optional status filter")
    public ResponseEntity<ApiResponse<PagedResponse<DeliveryResponse>>> getMyDeliveries(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) DeliveryStatus status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, Math.min(size, 50));
        Page<DeliveryResponse> data =
                deliveryService.getDeliveriesByAgent(principal.getUserId(), status, pageable);
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(data)));
    }

    // ─── Agent / Admin: update delivery status ────────────────────────────────

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('DELIVERY_AGENT','ADMIN')")
    @Operation(summary = "Update delivery status (EN_ROUTE → DELIVERED | FAILED)")
    public ResponseEntity<ApiResponse<DeliveryResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDeliveryStatusRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        // Pass agentId only for DELIVERY_AGENT role; ADMIN can update any delivery
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        Long agentId = isAdmin ? null : principal.getUserId();

        DeliveryResponse updated = deliveryService.updateDeliveryStatus(id, agentId, request);
        return ResponseEntity.ok(ApiResponse.success(updated, "Delivery status updated"));
    }
}
