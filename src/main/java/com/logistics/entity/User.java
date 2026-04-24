package com.logistics.entity;

import com.logistics.enums.UserRole;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Root of the user hierarchy.
 *
 * WHY SINGLE_TABLE inheritance?
 * ─────────────────────────────
 * We chose InheritanceType.SINGLE_TABLE because:
 *  1. Polymorphic queries ("find all users") require only ONE table scan – no JOINs.
 *  2. The extra columns added by sub-classes (address, vehicleInfo) are few,
 *     so the NULL overhead is acceptable.
 *  3. Admin has NO extra columns, making it a perfect fit for this strategy.
 *
 * Trade-off: Sub-class columns cannot have NOT NULL DB constraints (they'd
 * break rows belonging to other sub-classes). We enforce NOT NULL at the
 * application layer with @NotBlank / @NotNull on the request DTOs.
 *
 * DISCRIMINATOR COLUMN: "role" stores the enum string (ADMIN / CUSTOMER /
 * DELIVERY_AGENT). Using EnumType.STRING keeps the DB human-readable.
 */
@Entity
@Table(
    name = "users",
    indexes = {
        // Fast lookup by email / contactInfo is frequent in auth flows
        @Index(name = "idx_users_contact_info", columnList = "contactInfo"),
        @Index(name = "idx_users_role", columnList = "role")
    }
)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
@EntityListeners(AuditingEntityListener.class)   // auto-fill createdAt / updatedAt
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false)
    private String name;

    /**
     * Used as the login username (must be unique).
     * Called "contactInfo" per spec; in practice stores the e-mail address.
     */
    @Column(nullable = false, unique = true)
    private String contactInfo;

    /**
     * BCrypt-hashed password – never exposed in response DTOs.
     */
    @Column(nullable = false)
    private String password;

    // ─── Audit fields ────────────────────────────────────────────────────────

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ─── Constructors ────────────────────────────────────────────────────────

    protected User() {
        // JPA requires a no-arg constructor; protected prevents direct instantiation
    }

    protected User(String name, String contactInfo, String password) {
        this.name = name;
        this.contactInfo = contactInfo;
        this.password = password;
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    /**
     * Convenience accessor – returns the discriminator value as a UserRole enum.
     * Implemented by each concrete sub-class (they know their own role).
     */
    public abstract UserRole getRole();

    // ─── equals / hashCode ───────────────────────────────────────────────────
    // Business key: contactInfo is globally unique, so we use it for equality.

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User other)) return false;
        return Objects.equals(contactInfo, other.contactInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contactInfo);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
               "{userId=" + userId +
               ", name='" + name + '\'' +
               ", contactInfo='" + contactInfo + '\'' +
               '}';
    }
}
