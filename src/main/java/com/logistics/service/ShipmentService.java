package com.logistics.service;

import com.logistics.dto.request.AddTrackingInfoRequest;
import com.logistics.dto.response.ShipmentResponse;
import com.logistics.enums.ShipmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShipmentService {
    ShipmentResponse getShipmentById(Long id);
    List<ShipmentResponse> getShipmentsByOrder(Long orderId);
    Page<ShipmentResponse> getShipmentsByCustomer(Long customerId, Pageable pageable);
    Page<ShipmentResponse> getAllShipments(ShipmentStatus status, Pageable pageable);
    ShipmentResponse updateShipmentStatus(Long shipmentId, String rawStatus);
    ShipmentResponse addTrackingInfo(Long shipmentId, AddTrackingInfoRequest request);
}
