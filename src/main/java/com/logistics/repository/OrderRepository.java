package com.logistics.repository;

import com.logistics.entity.Order;
import com.logistics.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * Repository for Order entity.
 *
 * JPQL join fetches are used in detail queries to avoid N+1 problems when
 * loading associated shipments and payment in a single query.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /** Customer: view their own orders with pagination and optional status filter. */
    Page<Order> findByCustomer_UserId(Long customerId, Pageable pageable);

    Page<Order> findByCustomer_UserIdAndStatus(Long customerId, OrderStatus status, Pageable pageable);

    /** Admin: filter all orders by status. */
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    /** Admin: filter orders by date range. */
    Page<Order> findByOrderDateBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    /**
     * Fetch Order with its Shipments in one query to avoid lazy-load cascade.
     * This is the "detail view" query used by getOrderById.
     */
    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN FETCH o.shipments " +
           "LEFT JOIN FETCH o.payment " +
           "WHERE o.orderId = :orderId")
    java.util.Optional<Order> findByIdWithDetails(@Param("orderId") Long orderId);
}
