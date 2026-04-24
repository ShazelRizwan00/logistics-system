package com.logistics.dto.request;

import jakarta.validation.constraints.NotNull;

/** PATCH /api/orders/{id}/status */
public class UpdateOrderStatusRequest {

    @NotNull(message = "Status is required")
    private String status;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
