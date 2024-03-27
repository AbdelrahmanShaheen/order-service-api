package com.fawry.orderService.service;

import com.fawry.orderService.dto.order.OrderRequest;
import com.fawry.orderService.dto.order.OrderResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    void createOrder(OrderRequest orderRequest);
    List<OrderResponse> getOrdersByCustomerAndDateRange(String customerEmail, LocalDateTime startDate, LocalDateTime endDate);
}
