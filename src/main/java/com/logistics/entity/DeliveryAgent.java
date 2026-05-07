package com.logistics.entity;

import com.logistics.enums.UserRole;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
//final DeliveryAgent File
/**
 * Represents a Delivery Agent who performs last-mile deliveries.
 *
 * availabilityStatus = true  → agent is free / on-shift
 * availabilityStatus = false → agent is off-duty or fully loaded
 *
 * Relationship – Deliveries:
 *   One DeliveryAgent → Many Deliveries.
 *   We do NOT cascade deletes here: if an agent is removed, their historical
 *   delivery records must be preserved for audit purposes.
 */
@Entity
@DiscriminatorValue("DELIVERY_AGENT")
public class DeliveryAgent extends User {

    @Column(name = "vehicle_info")
    private String vehicleInfo;

    @Column(name = "availability_status")
    private boolean availabilityStatus = true;

    /**
     * FetchType.LAZY: loading an agent for auth or profile does not need all
     * their delivery history – load only when explicitly requested.
     */
    @OneToMany(
        mappedBy = "deliveryAgent",
        cascade = { CascadeType.PERSIST, CascadeType.MERGE },
        fetch = FetchType.LAZY
    )
    private List<Delivery> deliveries = new ArrayList<>();

    // ─── Constructors ────────────────────────────────────────────────────────

    public DeliveryAgent() {
        super();
    }

    public DeliveryAgent(String name, String contactInfo, String password,
                         String vehicleInfo, boolean availabilityStatus) {
        super(name, contactInfo, password);
        this.vehicleInfo = vehicleInfo;
        this.availabilityStatus = availabilityStatus;
    }

    // ─── UserRole accessor ───────────────────────────────────────────────────

    @Override
    public UserRole getRole() {
        return UserRole.DELIVERY_AGENT;
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public String getVehicleInfo() { return vehicleInfo; }
    public void setVehicleInfo(String vehicleInfo) { this.vehicleInfo = vehicleInfo; }

    public boolean isAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(boolean availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public List<Delivery> getDeliveries() { return deliveries; }
    public void setDeliveries(List<Delivery> deliveries) { this.deliveries = deliveries; }

    public void addDelivery(Delivery delivery) {
        deliveries.add(delivery);
        delivery.setDeliveryAgent(this);
    }

    // ─── equals / hashCode – delegated to parent ─────────────────────────────

    @Override
    public boolean equals(Object o) { return super.equals(o); }

    @Override
    public int hashCode() { return super.hashCode(); }
}
