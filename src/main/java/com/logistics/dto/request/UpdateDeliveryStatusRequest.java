package com.logistics.dto.request;

import jakarta.validation.constraints.NotNull;

/** PATCH /api/deliveries/{id}/status  (used by DeliveryAgent) */
public class UpdateDeliveryStatusRequest {

    @NotNull(message = "Status is required")
    private String status; // EN_ROUTE | DELIVERED | FAILED

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
