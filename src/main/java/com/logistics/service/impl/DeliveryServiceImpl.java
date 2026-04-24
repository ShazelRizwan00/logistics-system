package com.logistics.service.impl;

import com.logistics.dto.request.AssignAgentRequest;
import com.logistics.dto.request.UpdateDeliveryStatusRequest;
import com.logistics.dto.response.DeliveryResponse;
import com.logistics.entity.Delivery;
import com.logistics.entity.DeliveryAgent;
import com.logistics.entity.Shipment;
import com.logistics.enums.DeliveryStatus;
import com.logistics.enums.ShipmentStatus;
import com.logistics.exception.BusinessRuleException;
import com.logistics.exception.ResourceNotFoundException;
import com.logistics.repository.DeliveryAgentRepository;
import com.logistics.repository.DeliveryRepository;
import com.logistics.repository.ShipmentRepository;
import com.logistics.service.DeliveryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

/**
 * Business logic for Delivery management.
 *
 * Delivery Status Transitions:
 * ┌────────────┬──────────────────────────────┐
 * │ Current    │ Allowed next states          │
 * ├────────────┼──────────────────────────────┤
 * │ ASSIGNED   │ EN_ROUTE                     │
 * │ EN_ROUTE   │ DELIVERED, FAILED            │
 * │ FAILED     │ ASSIGNED (re-assignment)     │
 * │ DELIVERED  │ (terminal)                   │
 * └────────────┴──────────────────────────────┘
 *
 * Side-effects on Shipment:
 *  - When Delivery → DELIVERED  : Shipment status set to DELIVERED
 *  - When Delivery → EN_ROUTE   : Shipment status set to OUT_FOR_DELIVERY
 *  - When Delivery → FAILED     : Shipment status set to FAILED_DELIVERY
 */
@Service
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository      deliveryRepository;
    private final ShipmentRepository      shipmentRepository;
    private final DeliveryAgentRepository agentRepository;

    public DeliveryServiceImpl(DeliveryRepository      deliveryRepository,
                               ShipmentRepository      shipmentRepository,
                               DeliveryAgentRepository agentRepository) {
        this.deliveryRepository = deliveryRepository;
        this.shipmentRepository = shipmentRepository;
        this.agentRepository    = agentRepository;
    }

    // ─── Assign Agent ─────────────────────────────────────────────────────────

    @Override
    @Transactional
    public DeliveryResponse assignAgent(Long shipmentId, AssignAgentRequest req) {

        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", shipmentId));

        // A shipment can only be assigned a delivery when it has been picked up
        if (shipment.getStatus() == ShipmentStatus.DELIVERED ||
            shipment.getStatus() == ShipmentStatus.RETURNED) {
            throw new BusinessRuleException(
                    "Cannot assign agent: Shipment is already in terminal state " +
                    shipment.getStatus());
        }

        // Prevent double-assignment
        if (deliveryRepository.findByShipment_ShipmentId(shipmentId).isPresent()) {
            throw new BusinessRuleException(
                    "A delivery is already assigned for shipment " + shipmentId +
                    ". Re-assign via updateDeliveryStatus instead.");
        }

        DeliveryAgent agent = agentRepository.findById(req.getAgentId())
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryAgent", req.getAgentId()));

        if (!agent.isAvailabilityStatus()) {
            throw new BusinessRuleException(
                    "Agent " + req.getAgentId() + " is currently unavailable");
        }

        Delivery delivery = new Delivery(
                DeliveryStatus.ASSIGNED,
                null,           // deliveryTime set when actually delivered
                shipment,
                agent
        );

        // Mark agent busy if they now have active deliveries ≥ threshold
        long activeCount = deliveryRepository.countActiveDeliveriesByAgent(agent.getUserId());
        if (activeCount >= 4) {
            // Optional: mark as unavailable when load is high
            agent.setAvailabilityStatus(false);
            agentRepository.save(agent);
        }

        return DeliveryResponse.from(deliveryRepository.save(delivery));
    }

    // ─── Reads ────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public DeliveryResponse getDeliveryById(Long deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery", deliveryId));
        return DeliveryResponse.from(delivery);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryResponse getDeliveryByShipment(Long shipmentId) {
        Delivery delivery = deliveryRepository.findByShipment_ShipmentId(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Delivery for shipment", shipmentId));
        return DeliveryResponse.from(delivery);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DeliveryResponse> getDeliveriesByAgent(Long agentId, DeliveryStatus status,
                                                        Pageable pageable) {
        Page<Delivery> page = (status != null)
                ? deliveryRepository.findByDeliveryAgent_UserIdAndDeliveryStatus(
                        agentId, status, pageable)
                : deliveryRepository.findByDeliveryAgent_UserId(agentId, pageable);
        return page.map(DeliveryResponse::from);
    }

    // ─── Update Status ────────────────────────────────────────────────────────

    @Override
    @Transactional
    public DeliveryResponse updateDeliveryStatus(Long deliveryId, Long agentId,
                                                  UpdateDeliveryStatusRequest req) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery", deliveryId));

        // Ownership: only the assigned agent (or admin, checked at controller) can update
        if (agentId != null &&
            !delivery.getDeliveryAgent().getUserId().equals(agentId)) {
            throw new BusinessRuleException("You can only update your own deliveries");
        }

        DeliveryStatus newStatus = parseStatus(req.getStatus());
        validateDeliveryTransition(delivery.getDeliveryStatus(), newStatus);

        delivery.setDeliveryStatus(newStatus);

        // ── Side-effects on Shipment ──────────────────────────────────────────
        Shipment shipment = delivery.getShipment();
        switch (newStatus) {
            case EN_ROUTE   -> shipment.setStatus(ShipmentStatus.OUT_FOR_DELIVERY);
            case DELIVERED  -> {
                shipment.setStatus(ShipmentStatus.DELIVERED);
                delivery.setDeliveryTime(LocalDateTime.now());
                // Free up the agent when delivery completes
                DeliveryAgent agent = delivery.getDeliveryAgent();
                agent.setAvailabilityStatus(true);
                agentRepository.save(agent);
            }
            case FAILED     -> shipment.setStatus(ShipmentStatus.FAILED_DELIVERY);
            default         -> { /* ASSIGNED — no shipment side-effect */ }
        }

        shipmentRepository.save(shipment);
        return DeliveryResponse.from(deliveryRepository.save(delivery));
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private DeliveryStatus parseStatus(String raw) {
        try {
            return DeliveryStatus.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("Invalid delivery status: " + raw +
                    ". Valid values: ASSIGNED, EN_ROUTE, DELIVERED, FAILED");
        }
    }

    private void validateDeliveryTransition(DeliveryStatus current, DeliveryStatus next) {
        Set<DeliveryStatus> allowed = switch (current) {
            case ASSIGNED  -> EnumSet.of(DeliveryStatus.EN_ROUTE);
            case EN_ROUTE  -> EnumSet.of(DeliveryStatus.DELIVERED, DeliveryStatus.FAILED);
            case FAILED    -> EnumSet.of(DeliveryStatus.ASSIGNED);
            case DELIVERED -> throw new BusinessRuleException(
                    "Delivery is already in terminal state: DELIVERED");
        };

        if (!allowed.contains(next)) {
            throw new BusinessRuleException(
                    "Cannot transition Delivery from " + current + " to " + next +
                    ". Allowed: " + allowed);
        }
    }
}
