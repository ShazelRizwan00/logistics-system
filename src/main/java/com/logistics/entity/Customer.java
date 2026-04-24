package com.logistics.entity;

import com.logistics.enums.UserRole;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a Customer who can place and track orders.
 *
 * Discriminator value "CUSTOMER" maps to UserRole.CUSTOMER.
 *
 * Relationship – Orders:
 *   One Customer → Many Orders.
 *   CascadeType.ALL: if a customer is deleted, their orders are also deleted
 *   (orphanRemoval ensures orphaned Order rows are cleaned up).
 *   FetchType.LAZY: we never need the order list when loading a Customer for
 *   auth checks, so we avoid the N+1 query by loading lazily on demand.
 */
@Entity
@DiscriminatorValue("CUSTOMER")
public class Customer extends User {

    @Column(name = "address")
    private String address;

    /**
     * Bidirectional OneToMany.
     * mappedBy = "customer" means the FK lives on the Order side.
     * orphanRemoval = true: removing an Order from this list will DELETE it.
     */
    @OneToMany(
        mappedBy = "customer",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<Order> orders = new ArrayList<>();

    // ─── Constructors ────────────────────────────────────────────────────────

    public Customer() {
        super();
    }

    public Customer(String name, String contactInfo, String password, String address) {
        super(name, contactInfo, password);
        this.address = address;
    }

    // ─── UserRole accessor ───────────────────────────────────────────────────

    @Override
    public UserRole getRole() {
        return UserRole.CUSTOMER;
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public List<Order> getOrders() { return orders; }
    public void setOrders(List<Order> orders) { this.orders = orders; }

    /** Convenience helper to maintain bidirectional consistency. */
    public void addOrder(Order order) {
        orders.add(order);
        order.setCustomer(this);
    }

    public void removeOrder(Order order) {
        orders.remove(order);
        order.setCustomer(null);
    }

    // ─── equals / hashCode – delegated to parent (contactInfo) ───────────────

    @Override
    public boolean equals(Object o) { return super.equals(o); }

    @Override
    public int hashCode() { return super.hashCode(); }
}
