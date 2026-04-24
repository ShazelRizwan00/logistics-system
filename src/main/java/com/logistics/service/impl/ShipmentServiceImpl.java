package com.logistics.service.impl;

import com.logistics.dto.request.AddTrackingInfoRequest;
import com.logistics.dto.response.ShipmentResponse;
import com.logistics.entity.Shipment;
import com.logistics.entity.TrackingInfo;
import com.logistics.enums.ShipmentStatus;
import com.logistics.exception.BusinessRuleException;
import com.logistics.exception.ResourceNotFoundException;
import com.logistics.repository.ShipmentRepository;
import com.logistics.service.ShipmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Business logic for Shipment management.
 *
 * Shipment Status Transitions:
 * ┌────────────────┬────────────────────────────────────┐
 * │ Current        │ Allowed next states                │
 * ├────────────────┼────────────────────────────────────┤
 * │ CREATED        │ PICKED_UP                          │
 * │ PICKED_UP      │ IN_TRANSIT                         │
 * │ IN_TRANSIT     │ OUT_FOR_DELIVERY, RETURNED         │
 * │ OUT_FOR_DEL..  │ DELIVERED, FAILED_DELIVERY         │
 * │ FAILED_DEL..   │ IN_TRANSIT (retry)                 │
 * │ DELIVERED      │ (terminal)                         │
 * │ RETURNED       │ (terminal)                         │
 * └────────────────┴────────────────────────────────────┘
 *
 * Every status change also appends a TrackingInfo snapshot – giving a full
 * audit trail of the shipment journey.
 */
@Service
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;

    public ShipmentServiceImpl(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    // ─── Reads ────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public ShipmentResponse getShipmentById(Long id) {
        Shipment shipment = shipmentRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", id));
        return ShipmentResponse.from(shipment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentResponse> getShipmentsByOrder(Long orderId) {
        return shipmentRepository.findByOrder_OrderId(orderId).stream()
                .map(ShipmentResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShipmentResponse> getShipmentsByCustomer(Long customerId, Pageable pageable) {
        return shipmentRepository.findByCustomerId(customerId, pageable)
                .map(ShipmentResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShipmentResponse> getAllShipments(ShipmentStatus status, Pageable pageable) {
        Page<Shipment> page = (status != null)
                ? shipmentRepository.findByStatus(status, pageable)
                : shipmentRepository.findAll(pageable);
        return page.map(ShipmentResponse::from);
    }

    // ─── Mutations ────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ShipmentResponse updateShipmentStatus(Long shipmentId, String rawStatus) {
        Shipment shipment = shipmentRepository.findByIdWithDetails(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", shipmentId));

        ShipmentStatus newStatus = parseStatus(rawStatus);
        validateShipmentTransition(shipment.getStatus(), newStatus);

        shipment.setStatus(newStatus);

        // Auto-append a tracking snapshot whenever the status changes
        TrackingInfo snapshot = new TrackingInfo(
                shipment.getDestination(),      // simplified: use destination as current loc
                LocalDateTime.now(),
                newStatus,
                shipment
        );
        shipment.addTrackingInfo(snapshot);

        return ShipmentResponse.from(shipmentRepository.save(shipment));
    }

    @Override
    @Transactional
    public ShipmentResponse addTrackingInfo(Long shipmentId, AddTrackingInfoRequest req) {
        Shipment shipment = shipmentRepository.findByIdWithDetails(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", shipmentId));

        ShipmentStatus status = parseStatus(req.getStatus());
        validateShipmentTransition(shipment.getStatus(), status);

        TrackingInfo info = new TrackingInfo(
                req.getCurrentLocation(),
                LocalDateTime.now(),
                status,
                shipment
        );
        shipment.addTrackingInfo(info);
        shipment.setStatus(status);   // Also update the shipment's current status

        return ShipmentResponse.from(shipmentRepository.save(shipment));
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private ShipmentStatus parseStatus(String raw) {
        try {
            return ShipmentStatus.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("Invalid shipment status: " + raw);
        }
    }

    private void validateShipmentTransition(ShipmentStatus current, ShipmentStatus next) {
        Set<ShipmentStatus> allowed = switch (current) {
            case CREATED          -> EnumSet.of(ShipmentStatus.PICKED_UP);
            case PICKED_UP        -> EnumSet.of(ShipmentStatus.IN_TRANSIT);
            case IN_TRANSIT       -> EnumSet.of(ShipmentStatus.OUT_FOR_DELIVERY,
                                                ShipmentStatus.RETURNED);
            case OUT_FOR_DELIVERY -> EnumSet.of(ShipmentStatus.DELIVERED,
                                                ShipmentStatus.FAILED_DELIVERY);
            case FAILED_DELIVERY  -> EnumSet.of(ShipmentStatus.IN_TRANSIT);
            case DELIVERED, RETURNED ->
                    throw new BusinessRuleException(
                            "Shipment is in terminal state: " + current);
        };

        if (!allowed.contains(next)) {
            throw new BusinessRuleException(
                    "Cannot transition Shipment from " + current + " to " + next +
                    ". Allowed: " + allowed);
        }
    }
}
