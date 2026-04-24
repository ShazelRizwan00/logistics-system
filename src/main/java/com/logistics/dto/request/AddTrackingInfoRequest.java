package com.logistics.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** POST /api/shipments/{id}/tracking */
public class AddTrackingInfoRequest {

    @NotBlank(message = "Current location is required")
    private String currentLocation;

    @NotNull(message = "Status is required")
    private String status; // ShipmentStatus value

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
