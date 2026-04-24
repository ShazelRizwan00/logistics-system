package com.logistics.service;

import com.logistics.dto.request.CreateOrderRequest;
import com.logistics.dto.request.UpdateOrderStatusRequest;
import com.logistics.dto.response.OrderResponse;
import com.logistics.dto.response.PagedResponse;
import com.logistics.enums.OrderStatus;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request, String customerEmail);
    OrderResponse getOrderById(Long id);
    PagedResponse<OrderResponse> getMyOrders(String customerEmail, OrderStatus status, Pageable pageable);
    PagedResponse<OrderResponse> getAllOrders(OrderStatus status, Pageable pageable);
    OrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request);
    void cancelOrder(Long id, String requesterEmail);
}
