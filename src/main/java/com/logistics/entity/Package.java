package com.logistics.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a physical package within a Shipment.
 *
 * Naming note: "Package" is a reserved word in Java for java.lang.Package.
 * We use it here as a class name prefixed in its own package
 * (com.logistics.entity) which shadows nothing – this is safe.
 *
 * Relationship: Many Packages → One Shipment (FK: shipment_id).
 */
@Entity
@Table(
    name = "packages",
    indexes = {
        @Index(name = "idx_packages_shipment_id", columnList = "shipment_id")
    }
)
@EntityListeners(AuditingEntityListener.class)
public class Package {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_id")
    private Long packageId;

    /** Weight in kilograms. */
    @Column(nullable = false)
    private double weight;

    /**
     * Dimensions in "LxWxH cm" format, e.g. "30x20x15".
     * Stored as a plain String rather than three separate columns to keep
     * the schema simple; parse at the application layer when needed.
     */
    @Column(nullable = false, length = 50)
    private String dimensions;

    @Column(length = 500)
    private String description;

    // ─── Relationship ────────────────────────────────────────────────────────

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    // ─── Audit ───────────────────────────────────────────────────────────────

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ─── Constructors ────────────────────────────────────────────────────────

    public Package() {}

    public Package(double weight, String dimensions, String description, Shipment shipment) {
        this.weight = weight;
        this.dimensions = dimensions;
        this.description = description;
        this.shipment = shipment;
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public Long getPackageId() { return packageId; }
    public void setPackageId(Long packageId) { this.packageId = packageId; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public String getDimensions() { return dimensions; }
    public void setDimensions(String dimensions) { this.dimensions = dimensions; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Shipment getShipment() { return shipment; }
    public void setShipment(Shipment shipment) { this.shipment = shipment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // ─── equals / hashCode ───────────────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Package other)) return false;
        return Objects.equals(packageId, other.packageId);
    }

    @Override
    public int hashCode() { return Objects.hash(packageId); }
}
