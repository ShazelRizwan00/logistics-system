package com.logistics.service.impl;

import com.logistics.dto.request.CreateOrderRequest;
import com.logistics.dto.request.CreateShipmentRequest;
import com.logistics.dto.request.UpdateOrderStatusRequest;
import com.logistics.dto.response.OrderResponse;
import com.logistics.dto.response.PagedResponse;
import com.logistics.entity.Customer;
import com.logistics.entity.Order;
import com.logistics.entity.Package;
import com.logistics.entity.Payment;
import com.logistics.entity.Shipment;
import com.logistics.entity.TrackingInfo;
import com.logistics.enums.OrderStatus;
import com.logistics.enums.PaymentStatus;
import com.logistics.enums.ShipmentStatus;
import com.logistics.exception.BusinessRuleException;
import com.logistics.exception.ResourceNotFoundException;
import com.logistics.repository.CustomerRepository;
import com.logistics.repository.OrderRepository;
import com.logistics.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class OrderServiceImpl implements OrderService {

    // Valid transitions map: which statuses can follow which
    // Using a simple guard method below instead of a Map for readability.

    private final OrderRepository    orderRepository;
    private final CustomerRepository customerRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            CustomerRepository customerRepository) {
        this.orderRepository    = orderRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest req, String customerEmail) {
        // Resolve the authenticated customer
        Customer customer = customerRepository.findByContactInfo(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "email", customerEmail));

        // Build the Order
        Order order = new Order(LocalDateTime.now(), OrderStatus.PENDING, customer);

        // Build Payment
        Payment payment = new Payment(
                req.getPayment().getAmount(),
                req.getPayment().getMethod(),
                PaymentStatus.PENDING,
                order);
        order.setPayment(payment);

        // Build each Shipment + its Packages
        for (CreateShipmentRequest sr : req.getShipments()) {
            Shipment shipment = new Shipment(sr.getOrigin(), sr.getDestination(),
                                            ShipmentStatus.CREATED, order);
            shipment.addTrackingInfo(new TrackingInfo(
                    sr.getOrigin(),
                    LocalDateTime.now(),
                    ShipmentStatus.CREATED,
                    shipment));
            for (CreateShipmentRequest.PackageRequest pr : sr.getPackages()) {
                Package pkg = new Package(pr.getWeight(), pr.getDimensions(),
                                         pr.getDescription(), shipment);
                shipment.addPackage(pkg);
            }
            order.addShipment(shipment);
        }

        Order saved = orderRepository.save(order);
        // Reload with full detail for the response
        return OrderResponse.from(orderRepository.findByIdWithDetails(saved.getOrderId()).orElseThrow());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        return OrderResponse.from(order);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getMyOrders(String customerEmail,
                                                    OrderStatus status,
                                                    Pageable pageable) {
        Customer customer = customerRepository.findByContactInfo(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "email", customerEmail));

        Page<Order> page = (status != null)
                ? orderRepository.findByCustomer_UserIdAndStatus(customer.getUserId(), status, pageable)
                : orderRepository.findByCustomer_UserId(customer.getUserId(), pageable);

        return PagedResponse.of(page, OrderResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getAllOrders(OrderStatus status, Pageable pageable) {
        Page<Order> page = (status != null)
                ? orderRepository.findByStatus(status, pageable)
                : orderRepository.findAll(pageable);
        return PagedResponse.of(page, OrderResponse::from);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest req) {
        Order order = findOrderWithDetailsOrThrow(id);
        OrderStatus newStatus = parseStatus(req.getStatus());
        validateOrderTransition(order.getStatus(), newStatus);
        order.setStatus(newStatus);
        syncShipmentTracking(order, newStatus);
        return OrderResponse.from(orderRepository.save(order));
    }

    @Override
    @Transactional
    public void cancelOrder(Long id, String requesterEmail) {
        Order order = findOrderOrThrow(id);
        Customer requester = customerRepository.findByContactInfo(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "email", requesterEmail));
        if (!order.getCustomer().getUserId().equals(requester.getUserId())) {
            throw new BusinessRuleException("You can cancel only your own order.");
        }
        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new BusinessRuleException("Cannot cancel an already delivered order.");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessRuleException("Order is already cancelled.");
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Order findOrderOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
    }

    private Order findOrderWithDetailsOrThrow(Long id) {
        return orderRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
    }

    private OrderStatus parseStatus(String s) {
        try {
            return OrderStatus.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid order status: " + s);
        }
    }

    /**
     * Enforces the order status lifecycle rules.
     * PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED
     * Any non-DELIVERED state can transition to CANCELLED.
     */
    private void validateOrderTransition(OrderStatus current, OrderStatus next) {
        if (next == OrderStatus.CANCELLED) {
            if (current == OrderStatus.DELIVERED) {
                throw new BusinessRuleException("Cannot cancel a delivered order.");
            }
            return; // all other states can be cancelled
        }

        boolean valid = switch (current) {
            case PENDING     -> next == OrderStatus.CONFIRMED || next == OrderStatus.CANCELLED;
            case CONFIRMED   -> next == OrderStatus.PROCESSING;
            case PROCESSING  -> next == OrderStatus.SHIPPED;
            case SHIPPED     -> next == OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };

        if (!valid) {
            throw new BusinessRuleException(
                    "Invalid order status transition: " + current + " → " + next);
        }
    }

    private void syncShipmentTracking(Order order, OrderStatus orderStatus) {
        for (Shipment shipment : order.getShipments()) {
            switch (orderStatus) {
                case PROCESSING -> applyShipmentStatus(
                        shipment,
                        ShipmentStatus.PICKED_UP,
                        shipment.getOrigin());
                case SHIPPED -> applyShipmentStatus(
                        shipment,
                        ShipmentStatus.IN_TRANSIT,
                        "In transit from " + shipment.getOrigin());
                case DELIVERED -> {
                    applyShipmentStatus(
                            shipment,
                            ShipmentStatus.OUT_FOR_DELIVERY,
                            "Out for delivery to " + shipment.getDestination());
                    applyShipmentStatus(
                            shipment,
                            ShipmentStatus.DELIVERED,
                            shipment.getDestination());
                }
                case CANCELLED -> applyShipmentStatus(
                        shipment,
                        ShipmentStatus.RETURNED,
                        shipment.getOrigin());
                case PENDING, CONFIRMED -> {
                    // These order states do not imply physical shipment movement.
                }
            }
        }
    }

    private void applyShipmentStatus(Shipment shipment, ShipmentStatus status, String location) {
        if (shipment.getStatus() == status) {
            return;
        }

        shipment.setStatus(status);
        shipment.addTrackingInfo(new TrackingInfo(
                location,
                LocalDateTime.now(),
                status,
                shipment));
    }
}
