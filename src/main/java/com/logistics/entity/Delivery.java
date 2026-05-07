package com.logistics.entity;

import com.logistics.enums.DeliveryStatus;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;
//Final Delivery file
/**
 * Represents a last-mile delivery attempt by a DeliveryAgent.
 *
 * Relationships:
 * ┌──────────────────────────────────────────────────────────┐
 * │  One  Delivery → One  Shipment (FK: shipment_id)         │
 * │  Many Deliveries → One DeliveryAgent (FK: agent_id)      │
 * └──────────────────────────────────────────────────────────┘
 *
 * WHY OneToOne with Shipment (Delivery owns the FK)?
 * Shipment is the "parent" entity; Delivery is the subordinate record.
 * Placing the FK (shipment_id) on the Delivery table is more natural
 * and avoids a nullable FK on the Shipment table.
 */
@Entity
@Table(
    name = "deliveries",
    indexes = {
        @Index(name = "idx_deliveries_shipment_id", columnList = "shipment_id"),
        @Index(name = "idx_deliveries_agent_id",    columnList = "agent_id"),
        @Index(name = "idx_deliveries_status",      columnList = "deliveryStatus")
    }
)
@EntityListeners(AuditingEntityListener.class)
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long deliveryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false, length = 20)
    private DeliveryStatus deliveryStatus = DeliveryStatus.ASSIGNED;

    /** Actual or estimated delivery time; set when status becomes DELIVERED. */
    @Column(name = "delivery_time")
    private LocalDateTime deliveryTime;

    // ─── Relationships ───────────────────────────────────────────────────────

    /**
     * OneToOne – Delivery owns the FK (shipment_id).
     * fetch LAZY: loading delivery history doesn't always need the full Shipment graph.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shipment_id", nullable = false, unique = true)
    private Shipment shipment;

    /**
     * ManyToOne – many deliveries can be assigned to one agent over time.
     * fetch LAZY: loading a delivery for status updates doesn't need full agent profile.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private DeliveryAgent deliveryAgent;

    // ─── Audit fields ────────────────────────────────────────────────────────

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ─── Constructors ────────────────────────────────────────────────────────

    public Delivery() {}

    public Delivery(DeliveryStatus deliveryStatus, LocalDateTime deliveryTime,
                    Shipment shipment, DeliveryAgent deliveryAgent) {
        this.deliveryStatus = deliveryStatus;
        this.deliveryTime = deliveryTime;
        this.shipment = shipment;
        this.deliveryAgent = deliveryAgent;
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public Long getDeliveryId() { return deliveryId; }
    public void setDeliveryId(Long deliveryId) { this.deliveryId = deliveryId; }

    public DeliveryStatus getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public LocalDateTime getDeliveryTime() { return deliveryTime; }
    public void setDeliveryTime(LocalDateTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Shipment getShipment() { return shipment; }
    public void setShipment(Shipment shipment) { this.shipment = shipment; }

    public DeliveryAgent getDeliveryAgent() { return deliveryAgent; }
    public void setDeliveryAgent(DeliveryAgent deliveryAgent) {
        this.deliveryAgent = deliveryAgent;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // ─── equals / hashCode ───────────────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Delivery other)) return false;
        return Objects.equals(deliveryId, other.deliveryId);
    }

    @Override
    public int hashCode() { return Objects.hash(deliveryId); }

    @Override
    public String toString() {
        return "Delivery{deliveryId=" + deliveryId + ", status=" + deliveryStatus + '}';
    }
}
