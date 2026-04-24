package com.logistics.enums;

/**
 * Lifecycle states for a Payment.
 *
 * Valid transitions:
 *   PENDING → COMPLETED
 *           ↘ FAILED
 *   COMPLETED → REFUNDED
 */
public enum PaymentStatus {

    /** Payment initiated but not yet confirmed. */
    PENDING,

    /** Payment successfully processed. */
    COMPLETED,

    /** Payment processing failed. */
    FAILED,

    /** Payment refunded back to the customer. */
    REFUNDED
}
