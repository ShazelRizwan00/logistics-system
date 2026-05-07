package com.logistics.entity;
import com.logistics.enums.UserRole;
import jakarta.persistence.*;
/**
 * Represents an Admin user.
 *
 * Admin has no additional fields beyond the base User, but it needs its own
 * JPA entity class so that:
 *  1. Spring Security can distinguish the ADMIN role in token claims.
 *  2. Future fields (e.g. adminLevel, department) can be added cleanly.
 add a comment
 */
@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {

    // ─── Constructors ────────────────────────────────────────────────────────

    public Admin() {
        super();
    }

    public Admin(String name, String contactInfo, String password) {
        super(name, contactInfo, password);
    }

    // ─── UserRole accessor ───────────────────────────────────────────────────

    @Override
    public UserRole getRole() {
        return UserRole.ADMIN;
    }

    // ─── equals / hashCode – delegated to parent ─────────────────────────────
    @Override
    public boolean equals(Object o) { return super.equals(o); }
    @Override
    public int hashCode() { return super.hashCode(); }
}
