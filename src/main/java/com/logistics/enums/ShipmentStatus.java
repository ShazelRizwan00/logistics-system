package com.logistics.enums;

/**
 * Lifecycle states for a Shipment (and reused in TrackingInfo snapshots).
 *
 * Valid transitions:
 *   CREATED → PICKED_UP → IN_TRANSIT → OUT_FOR_DELIVERY → DELIVERED
 *                                                        ↘ FAILED_DELIVERY → IN_TRANSIT (retry)
 *                         ↘ RETURNED (at any stage before delivery)
 */
public enum ShipmentStatus {

    /** Shipment record created; awaiting carrier pick-up. */
    CREATED,

    /** Carrier has collected the packages from origin. */
    PICKED_UP,

    /** Packages are in transit between hubs. */
    IN_TRANSIT,

    /** Packages are on the last-mile vehicle heading to destination. */
    OUT_FOR_DELIVERY,

    /** Successfully delivered to recipient. */
    DELIVERED,

    /** Delivery attempted but failed (recipient absent, address issue, etc.). */
    FAILED_DELIVERY,

    /** Shipment is being returned to sender. */
    RETURNED
}
