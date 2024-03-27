package com.fawry.orderService.controller;

import com.fawry.orderService.dto.order.OrderRequest;
import com.fawry.orderService.dto.order.OrderResponse;
import com.fawry.orderService.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/complete-checkout")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void completeCheckout(@RequestBody @Valid OrderRequest orderRequest){
        orderService.createOrder(orderRequest);
    }
    @GetMapping("/by-email-and-range-date")
    List<OrderResponse> getOrdersByCustomerAndDateRange(
            @RequestParam  String customerEmail,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate){
        return orderService.getOrdersByCustomerAndDateRange(customerEmail ,startDate ,endDate);
    }
}
