package com.logistics.repository;

import com.logistics.entity.TrackingInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackingInfoRepository extends JpaRepository<TrackingInfo, Long> {

    /** Returns the full timeline for a shipment, ordered by timestamp ASC. */
    List<TrackingInfo> findByShipment_ShipmentIdOrderByTimestampAsc(Long shipmentId);
}
