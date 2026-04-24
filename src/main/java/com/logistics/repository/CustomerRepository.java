package com.logistics.repository;

import com.logistics.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository specifically for Customer sub-type.
 * Spring Data generates a WHERE clause for discriminator = 'CUSTOMER' automatically.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByContactInfo(String contactInfo);

    Page<Customer> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
