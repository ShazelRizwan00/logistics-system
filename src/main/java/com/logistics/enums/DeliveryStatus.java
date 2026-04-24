package com.logistics.enums;

/**
 * Lifecycle states for a Delivery (last-mile delivery attempt).
 *
 * Valid transitions:
 *   ASSIGNED → EN_ROUTE → DELIVERED
 *                       ↘ FAILED → ASSIGNED (re-attempt)
 */
public enum DeliveryStatus {

    /** Delivery task created and assigned to an agent; not yet started. */
    ASSIGNED,

    /** Delivery agent is en-route to the destination. */
    EN_ROUTE,

    /** Package(s) successfully handed off to the recipient. */
    DELIVERED,

    /** Delivery attempt failed; will be rescheduled. */
    FAILED
}
