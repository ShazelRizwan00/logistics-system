package com.logistics.entity;

import com.logistics.enums.OrderStatus;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a Customer's purchase order that drives the shipment pipeline.
 *
 * Relationships:
 * ┌──────────────────────────────────────────────────────┐
 * │  Many Orders → One Customer  (FK: customer_id)       │
 * │  One Order   → Many Shipments                        │
 * │  One Order   → One Payment   (cascade ALL)           │
 * └──────────────────────────────────────────────────────┘
 *
 * Status lifecycle: see OrderStatus enum.
 */
@Entity
@Table(
    name = "orders",
    indexes = {
        @Index(name = "idx_orders_customer_id", columnList = "customer_id"),
        @Index(name = "idx_orders_status",      columnList = "status"),
        @Index(name = "idx_orders_order_date",  columnList = "orderDate")
    }
)
@EntityListeners(AuditingEntityListener.class)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    /**
     * EnumType.STRING: storing enum name is robust against enum reordering.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PENDING;

    // ─── Relationships ───────────────────────────────────────────────────────

    /**
     * ManyToOne (eager load) – we almost always need the customer when
     * displaying or processing an order, so EAGER is acceptable here.
     * The FK is stored on the orders table (customer_id).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    /**
     * OneToMany Shipments – a single order can be split into multiple
     * shipments (e.g. different warehouse origins).
     * CascadeType.ALL + orphanRemoval: shipments are owned by this order.
     */
    @OneToMany(
        mappedBy = "order",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<Shipment> shipments = new ArrayList<>();

    /**
     * OneToOne Payment – cascade ALL so that saving an order also saves its
     * payment record. mappedBy = "order" means Payment holds the FK.
     */
    @OneToOne(
        mappedBy = "order",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private Payment payment;

    // ─── Audit fields ────────────────────────────────────────────────────────

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ─── Constructors ────────────────────────────────────────────────────────

    public Order() {}

    public Order(LocalDateTime orderDate, OrderStatus status, Customer customer) {
        this.orderDate = orderDate;
        this.status = status;
        this.customer = customer;
    }

    // ─── Bidirectional helpers ───────────────────────────────────────────────

    public void addShipment(Shipment shipment) {
        shipments.add(shipment);
        shipment.setOrder(this);
    }

    public void removeShipment(Shipment shipment) {
        shipments.remove(shipment);
        shipment.setOrder(null);
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public List<Shipment> getShipments() { return shipments; }
    public void setShipments(List<Shipment> shipments) { this.shipments = shipments; }

    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // ─── equals / hashCode ───────────────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order other)) return false;
        return Objects.equals(orderId, other.orderId);
    }

    @Override
    public int hashCode() { return Objects.hash(orderId); }

    @Override
    public String toString() {
        return "Order{orderId=" + orderId + ", status=" + status + '}';
    }
}
