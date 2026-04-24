package com.logistics.enums;

/**
 * Lifecycle states for an Order.
 *
 * Valid transitions:
 *   PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED
 *                                              ↘ CANCELLED  (from any state before DELIVERED)
 *
 * This enum is stored as a STRING in the DB (EnumType.STRING) so that
 * adding new values later doesn't break existing rows (ordinal storage
 * would shift all values if a new enum constant were inserted mid-list).
 */
public enum OrderStatus {

    /** Order created but not yet confirmed by the system/admin. */
    PENDING,

    /** Payment verified; order accepted into the pipeline. */
    CONFIRMED,

    /** Warehouse is preparing / packing the order. */
    PROCESSING,

    /** At least one shipment has been dispatched. */
    SHIPPED,

    /** All shipments delivered successfully. */
    DELIVERED,

    /** Order cancelled before delivery. */
    CANCELLED
}
