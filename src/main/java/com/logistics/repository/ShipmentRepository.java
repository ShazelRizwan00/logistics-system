package com.logistics.repository;

import com.logistics.entity.Shipment;
import com.logistics.enums.ShipmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    List<Shipment> findByOrder_OrderId(Long orderId);

    Page<Shipment> findByStatus(ShipmentStatus status, Pageable pageable);

    /** Shipment detail; related collections are initialized in the service transaction. */
    @Query("SELECT s FROM Shipment s WHERE s.shipmentId = :id")
    Optional<Shipment> findByIdWithDetails(@Param("id") Long id);

    /** For a customer to track their own shipments via order ownership. */
    @Query("SELECT s FROM Shipment s WHERE s.order.customer.userId = :customerId")
    Page<Shipment> findByCustomerId(@Param("customerId") Long customerId, Pageable pageable);
}
