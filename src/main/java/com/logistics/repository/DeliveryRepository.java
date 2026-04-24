package com.logistics.repository;

import com.logistics.entity.Delivery;
import com.logistics.enums.DeliveryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Optional<Delivery> findByShipment_ShipmentId(Long shipmentId);

    /** DeliveryAgent: view their assigned deliveries. */
    Page<Delivery> findByDeliveryAgent_UserId(Long agentId, Pageable pageable);

    Page<Delivery> findByDeliveryAgent_UserIdAndDeliveryStatus(
            Long agentId, DeliveryStatus status, Pageable pageable);

    /** Count active deliveries for an agent (used in assignment logic). */
    @Query("SELECT COUNT(d) FROM Delivery d " +
           "WHERE d.deliveryAgent.userId = :agentId " +
           "AND d.deliveryStatus IN ('ASSIGNED', 'EN_ROUTE')")
    long countActiveDeliveriesByAgent(@Param("agentId") Long agentId);
}
