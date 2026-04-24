package com.logistics.repository;

import com.logistics.entity.DeliveryAgent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for DeliveryAgent sub-type.
 */
@Repository
public interface DeliveryAgentRepository extends JpaRepository<DeliveryAgent, Long> {

    Optional<DeliveryAgent> findByContactInfo(String contactInfo);

    /** Find all available agents for assignment. */
    List<DeliveryAgent> findByAvailabilityStatusTrue();

    Page<DeliveryAgent> findByAvailabilityStatus(boolean availabilityStatus, Pageable pageable);
}
