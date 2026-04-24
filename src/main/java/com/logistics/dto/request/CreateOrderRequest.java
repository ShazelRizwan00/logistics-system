package com.logistics.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/** Request body for POST /api/orders */
public class CreateOrderRequest {

    @NotNull(message = "Payment info is required")
    @Valid
    private PaymentRequest payment;

    /** At least one shipment must be specified when placing an order. */
    @NotEmpty(message = "At least one shipment is required")
    @Valid
    private List<CreateShipmentRequest> shipments;

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public PaymentRequest getPayment() { return payment; }
    public void setPayment(PaymentRequest payment) { this.payment = payment; }

    public List<CreateShipmentRequest> getShipments() { return shipments; }
    public void setShipments(List<CreateShipmentRequest> shipments) { this.shipments = shipments; }

    // ─── Nested DTOs ─────────────────────────────────────────────────────────

    public static class PaymentRequest {

        @Positive(message = "Amount must be positive")
        private double amount;

        @NotNull(message = "Payment method is required")
        private String method;

        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }

        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }
    }
}
