package com.logistics.repository;

import com.logistics.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {

    List<Package> findByShipment_ShipmentId(Long shipmentId);
}
