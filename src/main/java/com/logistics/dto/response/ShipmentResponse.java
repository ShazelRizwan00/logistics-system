package com.logistics.dto.response;

import com.logistics.entity.Package;
import com.logistics.entity.Shipment;
import com.logistics.entity.TrackingInfo;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/** Full shipment detail response, including packages and tracking history. */
public class ShipmentResponse {

    private Long shipmentId;
    private String origin;
    private String destination;
    private String status;
    private Long orderId;
    private List<PackageDetail> packages;
    private List<TrackingEvent> trackingHistory;
    private DeliveryDetail delivery;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ShipmentResponse from(Shipment s) {
        ShipmentResponse r = new ShipmentResponse();
        r.shipmentId      = s.getShipmentId();
        r.origin          = s.getOrigin();
        r.destination     = s.getDestination();
        r.status          = s.getStatus().name();
        r.createdAt       = s.getCreatedAt();
        r.updatedAt       = s.getUpdatedAt();

        if (s.getOrder() != null) {
            r.orderId = s.getOrder().getOrderId();
        }

        r.packages = (s.getPackages() != null)
                ? s.getPackages().stream().map(PackageDetail::from).collect(Collectors.toList())
                : Collections.emptyList();

        r.trackingHistory = (s.getTrackingHistory() != null)
                ? s.getTrackingHistory().stream().map(TrackingEvent::from).collect(Collectors.toList())
                : Collections.emptyList();

        if (s.getDelivery() != null) {
            r.delivery = DeliveryDetail.from(s.getDelivery());
        }

        return r;
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public Long getShipmentId() { return shipmentId; }
    public void setShipmentId(Long shipmentId) { this.shipmentId = shipmentId; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public List<PackageDetail> getPackages() { return packages; }
    public void setPackages(List<PackageDetail> packages) { this.packages = packages; }

    public List<TrackingEvent> getTrackingHistory() { return trackingHistory; }
    public void setTrackingHistory(List<TrackingEvent> trackingHistory) { this.trackingHistory = trackingHistory; }

    public DeliveryDetail getDelivery() { return delivery; }
    public void setDelivery(DeliveryDetail delivery) { this.delivery = delivery; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // ─── Nested DTOs ─────────────────────────────────────────────────────────

    public static class PackageDetail {
        private Long packageId;
        private double weight;
        private String dimensions;
        private String description;

        public static PackageDetail from(Package p) {
            PackageDetail d = new PackageDetail();
            d.packageId   = p.getPackageId();
            d.weight      = p.getWeight();
            d.dimensions  = p.getDimensions();
            d.description = p.getDescription();
            return d;
        }

        public Long getPackageId() { return packageId; }
        public void setPackageId(Long packageId) { this.packageId = packageId; }
        public double getWeight() { return weight; }
        public void setWeight(double weight) { this.weight = weight; }
        public String getDimensions() { return dimensions; }
        public void setDimensions(String dimensions) { this.dimensions = dimensions; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class TrackingEvent {
        private Long trackingId;
        private String currentLocation;
        private LocalDateTime timestamp;
        private String status;

        public static TrackingEvent from(TrackingInfo t) {
            TrackingEvent e = new TrackingEvent();
            e.trackingId      = t.getTrackingId();
            e.currentLocation = t.getCurrentLocation();
            e.timestamp       = t.getTimestamp();
            e.status          = t.getStatus().name();
            return e;
        }

        public Long getTrackingId() { return trackingId; }
        public void setTrackingId(Long trackingId) { this.trackingId = trackingId; }
        public String getCurrentLocation() { return currentLocation; }
        public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class DeliveryDetail {
        private Long deliveryId;
        private String deliveryStatus;
        private LocalDateTime deliveryTime;
        private Long agentId;
        private String agentName;

        public static DeliveryDetail from(com.logistics.entity.Delivery d) {
            DeliveryDetail dd = new DeliveryDetail();
            dd.deliveryId     = d.getDeliveryId();
            dd.deliveryStatus = d.getDeliveryStatus().name();
            dd.deliveryTime   = d.getDeliveryTime();
            if (d.getDeliveryAgent() != null) {
                dd.agentId   = d.getDeliveryAgent().getUserId();
                dd.agentName = d.getDeliveryAgent().getName();
            }
            return dd;
        }

        public Long getDeliveryId() { return deliveryId; }
        public void setDeliveryId(Long deliveryId) { this.deliveryId = deliveryId; }
        public String getDeliveryStatus() { return deliveryStatus; }
        public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }
        public LocalDateTime getDeliveryTime() { return deliveryTime; }
        public void setDeliveryTime(LocalDateTime deliveryTime) { this.deliveryTime = deliveryTime; }
        public Long getAgentId() { return agentId; }
        public void setAgentId(Long agentId) { this.agentId = agentId; }
        public String getAgentName() { return agentName; }
        public void setAgentName(String agentName) { this.agentName = agentName; }
    }
}
