package com.logistics.service;

import com.logistics.dto.request.AssignAgentRequest;
import com.logistics.dto.request.UpdateDeliveryStatusRequest;
import com.logistics.dto.response.DeliveryResponse;
import com.logistics.enums.DeliveryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeliveryService {
    DeliveryResponse assignAgent(Long shipmentId, AssignAgentRequest req);
    DeliveryResponse getDeliveryById(Long deliveryId);
    DeliveryResponse getDeliveryByShipment(Long shipmentId);
    Page<DeliveryResponse> getDeliveriesByAgent(Long agentId, DeliveryStatus status, Pageable pageable);
    DeliveryResponse updateDeliveryStatus(Long deliveryId, Long agentId, UpdateDeliveryStatusRequest req);
}
