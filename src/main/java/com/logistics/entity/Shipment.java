package com.logistics.entity;

import com.logistics.enums.ShipmentStatus;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a physical consignment moving from origin to destination.
 *
 * One Order can produce multiple Shipments (different warehouses / partial
 * fulfilment). Each Shipment tracks its own status independently.
 *
 * Relationships:
 * ┌────────────────────────────────────────────────────────────┐
 * │  Many Shipments → One Order    (FK: order_id)              │
 * │  One  Shipment  → One Delivery (cascade ALL)               │
 * │  One  Shipment  → Many Packages (cascade ALL)              │
 * │  One  Shipment  → Many TrackingInfo (cascade ALL)          │
 * └────────────────────────────────────────────────────────────┘
 */
@Entity
@Table(
    name = "shipments",
    indexes = {
        @Index(name = "idx_shipments_order_id", columnList = "order_id"),
        @Index(name = "idx_shipments_status",   columnList = "status")
    }
)
@EntityListeners(AuditingEntityListener.class)
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipment_id")
    private Long shipmentId;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String destination;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private ShipmentStatus status = ShipmentStatus.CREATED;

    // ─── Relationships ───────────────────────────────────────────────────────

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * One Shipment → One Delivery.
     * CascadeType.ALL: creating/deleting a shipment manages its delivery.
     * fetch LAZY: delivery details are only needed in specific use-cases.
     */
    @OneToOne(
        mappedBy = "shipment",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private Delivery delivery;

    /**
     * One Shipment → Many Packages.
     * CascadeType.ALL + orphanRemoval: packages belong exclusively to this shipment.
     */
    @OneToMany(
        mappedBy = "shipment",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<Package> packages = new ArrayList<>();

    /**
     * One Shipment → Many TrackingInfo (timeline).
     * Ordered by timestamp for chronological display.
     */
    @OneToMany(
        mappedBy = "shipment",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @OrderBy("timestamp ASC")
    private List<TrackingInfo> trackingHistory = new ArrayList<>();

    // ─── Audit fields ────────────────────────────────────────────────────────

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ─── Constructors ────────────────────────────────────────────────────────

    public Shipment() {}

    public Shipment(String origin, String destination, ShipmentStatus status, Order order) {
        this.origin = origin;
        this.destination = destination;
        this.status = status;
        this.order = order;
    }

    // ─── Bidirectional helpers ───────────────────────────────────────────────

    public void addPackage(Package pkg) {
        packages.add(pkg);
        pkg.setShipment(this);
    }

    public void addTrackingInfo(TrackingInfo info) {
        trackingHistory.add(info);
        info.setShipment(this);
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public Long getShipmentId() { return shipmentId; }
    public void setShipmentId(Long shipmentId) { this.shipmentId = shipmentId; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public ShipmentStatus getStatus() { return status; }
    public void setStatus(ShipmentStatus status) { this.status = status; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Delivery getDelivery() { return delivery; }
    public void setDelivery(Delivery delivery) { this.delivery = delivery; }

    public List<Package> getPackages() { return packages; }
    public void setPackages(List<Package> packages) { this.packages = packages; }

    public List<TrackingInfo> getTrackingHistory() { return trackingHistory; }
    public void setTrackingHistory(List<TrackingInfo> trackingHistory) {
        this.trackingHistory = trackingHistory;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // ─── equals / hashCode ───────────────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Shipment other)) return false;
        return Objects.equals(shipmentId, other.shipmentId);
    }

    @Override
    public int hashCode() { return Objects.hash(shipmentId); }

    @Override
    public String toString() {
        return "Shipment{shipmentId=" + shipmentId + ", status=" + status + '}';
    }
}
