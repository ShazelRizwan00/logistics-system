package com.logistics.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.util.List;

/** Nested inside CreateOrderRequest and also used standalone for admin shipment creation. */
public class CreateShipmentRequest {

    @NotBlank(message = "Origin is required")
    private String origin;

    @NotBlank(message = "Destination is required")
    private String destination;

    @NotEmpty(message = "At least one package is required per shipment")
    @Valid
    private List<PackageRequest> packages;

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public List<PackageRequest> getPackages() { return packages; }
    public void setPackages(List<PackageRequest> packages) { this.packages = packages; }

    // ─── Nested DTO ──────────────────────────────────────────────────────────

    public static class PackageRequest {

        @Positive(message = "Weight must be positive")
        private double weight;

        @NotBlank(message = "Dimensions are required")
        private String dimensions;

        private String description;

        public double getWeight() { return weight; }
        public void setWeight(double weight) { this.weight = weight; }

        public String getDimensions() { return dimensions; }
        public void setDimensions(String dimensions) { this.dimensions = dimensions; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
