package com.logistics.entity;

import com.logistics.enums.ShipmentStatus;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Immutable snapshot of a Shipment's location and status at a point in time.
 *
 * Each time a shipment moves to a new hub or changes status, a new
 * TrackingInfo record is inserted (append-only / event-log pattern).
 * This gives a full audit trail of the shipment's journey.
 *
 * WHY no updatedAt?  TrackingInfo is immutable once created; it represents
 * a historical fact. We only store createdAt (= the event timestamp).
 *
 * Relationship: Many TrackingInfo → One Shipment (FK: shipment_id).
 */
@Entity
@Table(
    name = "tracking_info",
    indexes = {
        @Index(name = "idx_tracking_shipment_id", columnList = "shipment_id"),
        @Index(name = "idx_tracking_timestamp",   columnList = "timestamp")
    }
)
@EntityListeners(AuditingEntityListener.class)
public class TrackingInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tracking_id")
    private Long trackingId;

    @Column(name = "current_location", nullable = false)
    private String currentLocation;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private ShipmentStatus status;

    // ─── Relationship ────────────────────────────────────────────────────────

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    // ─── Audit ───────────────────────────────────────────────────────────────

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ─── Constructors ────────────────────────────────────────────────────────

    public TrackingInfo() {}

    public TrackingInfo(String currentLocation, LocalDateTime timestamp,
                        ShipmentStatus status, Shipment shipment) {
        this.currentLocation = currentLocation;
        this.timestamp = timestamp;
        this.status = status;
        this.shipment = shipment;
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public Long getTrackingId() { return trackingId; }
    public void setTrackingId(Long trackingId) { this.trackingId = trackingId; }

    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public ShipmentStatus getStatus() { return status; }
    public void setStatus(ShipmentStatus status) { this.status = status; }

    public Shipment getShipment() { return shipment; }
    public void setShipment(Shipment shipment) { this.shipment = shipment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // ─── equals / hashCode ───────────────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrackingInfo other)) return false;
        return Objects.equals(trackingId, other.trackingId);
    }

    @Override
    public int hashCode() { return Objects.hash(trackingId); }
}
