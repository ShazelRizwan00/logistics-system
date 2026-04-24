package com.logistics.dto.response;

import com.logistics.entity.Customer;
import com.logistics.entity.DeliveryAgent;
import com.logistics.entity.User;

import java.time.LocalDateTime;

/**
 * Safe user representation that NEVER exposes the password hash.
 */
public class UserResponse {

    private Long userId;
    private String name;
    private String contactInfo;
    private String role;

    // Sub-type specific fields (null when not applicable)
    private String address;        // Customer only
    private String vehicleInfo;    // DeliveryAgent only
    private Boolean available;     // DeliveryAgent only

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** Static factory – converts any User sub-type to this DTO. */
    public static UserResponse from(User user) {
        UserResponse r = new UserResponse();
        r.userId = user.getUserId();
        r.name = user.getName();
        r.contactInfo = user.getContactInfo();
        r.role = user.getRole().name();
        r.createdAt = user.getCreatedAt();
        r.updatedAt = user.getUpdatedAt();

        if (user instanceof Customer c) {
            r.address = c.getAddress();
        } else if (user instanceof DeliveryAgent a) {
            r.vehicleInfo = a.getVehicleInfo();
            r.available = a.isAvailabilityStatus();
        }
        return r;
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getVehicleInfo() { return vehicleInfo; }
    public void setVehicleInfo(String vehicleInfo) { this.vehicleInfo = vehicleInfo; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
