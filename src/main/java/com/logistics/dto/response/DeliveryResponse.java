package com.logistics.dto.response;

import com.logistics.entity.Delivery;

import java.time.LocalDateTime;

public class DeliveryResponse {

    private Long deliveryId;
    private String deliveryStatus;
    private LocalDateTime deliveryTime;
    private Long shipmentId;
    private String shipmentOrigin;
    private String shipmentDestination;
    private Long agentId;
    private String agentName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DeliveryResponse from(Delivery d) {
        DeliveryResponse r = new DeliveryResponse();
        r.deliveryId     = d.getDeliveryId();
        r.deliveryStatus = d.getDeliveryStatus().name();
        r.deliveryTime   = d.getDeliveryTime();
        r.createdAt      = d.getCreatedAt();
        r.updatedAt      = d.getUpdatedAt();

        if (d.getShipment() != null) {
            r.shipmentId          = d.getShipment().getShipmentId();
            r.shipmentOrigin      = d.getShipment().getOrigin();
            r.shipmentDestination = d.getShipment().getDestination();
        }
        if (d.getDeliveryAgent() != null) {
            r.agentId   = d.getDeliveryAgent().getUserId();
            r.agentName = d.getDeliveryAgent().getName();
        }
        return r;
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public Long getDeliveryId() { return deliveryId; }
    public void setDeliveryId(Long deliveryId) { this.deliveryId = deliveryId; }

    public String getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }

    public LocalDateTime getDeliveryTime() { return deliveryTime; }
    public void setDeliveryTime(LocalDateTime deliveryTime) { this.deliveryTime = deliveryTime; }

    public Long getShipmentId() { return shipmentId; }
    public void setShipmentId(Long shipmentId) { this.shipmentId = shipmentId; }

    public String getShipmentOrigin() { return shipmentOrigin; }
    public void setShipmentOrigin(String shipmentOrigin) { this.shipmentOrigin = shipmentOrigin; }

    public String getShipmentDestination() { return shipmentDestination; }
    public void setShipmentDestination(String shipmentDestination) { this.shipmentDestination = shipmentDestination; }

    public Long getAgentId() { return agentId; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }

    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
