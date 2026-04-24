package com.logistics.dto.response;

import com.logistics.entity.Order;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/** Response DTO for Order – includes nested shipment summaries and payment. */
public class OrderResponse {

    private Long orderId;
    private LocalDateTime orderDate;
    private String status;
    private Long customerId;
    private String customerName;
    private List<ShipmentSummary> shipments;
    private PaymentSummary payment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static OrderResponse from(Order order) {
        OrderResponse r = new OrderResponse();
        r.orderId      = order.getOrderId();
        r.orderDate    = order.getOrderDate();
        r.status       = order.getStatus().name();
        r.createdAt    = order.getCreatedAt();
        r.updatedAt    = order.getUpdatedAt();

        if (order.getCustomer() != null) {
            r.customerId   = order.getCustomer().getUserId();
            r.customerName = order.getCustomer().getName();
        }

        // Guard against uninitialized lazy collection
        if (order.getShipments() != null) {
            r.shipments = order.getShipments().stream()
                    .map(ShipmentSummary::from)
                    .collect(Collectors.toList());
        } else {
            r.shipments = Collections.emptyList();
        }

        if (order.getPayment() != null) {
            r.payment = PaymentSummary.from(order.getPayment());
        }

        return r;
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public List<ShipmentSummary> getShipments() { return shipments; }
    public void setShipments(List<ShipmentSummary> shipments) { this.shipments = shipments; }

    public PaymentSummary getPayment() { return payment; }
    public void setPayment(PaymentSummary payment) { this.payment = payment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // ─── Nested summary classes ───────────────────────────────────────────────

    public static class ShipmentSummary {
        private Long shipmentId;
        private String origin;
        private String destination;
        private String status;

        public static ShipmentSummary from(com.logistics.entity.Shipment s) {
            ShipmentSummary ss = new ShipmentSummary();
            ss.shipmentId  = s.getShipmentId();
            ss.origin      = s.getOrigin();
            ss.destination = s.getDestination();
            ss.status      = s.getStatus().name();
            return ss;
        }

        public Long getShipmentId() { return shipmentId; }
        public void setShipmentId(Long shipmentId) { this.shipmentId = shipmentId; }
        public String getOrigin() { return origin; }
        public void setOrigin(String origin) { this.origin = origin; }
        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class PaymentSummary {
        private Long paymentId;
        private double amount;
        private String method;
        private String status;

        public static PaymentSummary from(com.logistics.entity.Payment p) {
            PaymentSummary ps = new PaymentSummary();
            ps.paymentId = p.getPaymentId();
            ps.amount    = p.getAmount();
            ps.method    = p.getMethod();
            ps.status    = p.getStatus().name();
            return ps;
        }

        public Long getPaymentId() { return paymentId; }
        public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }
        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
