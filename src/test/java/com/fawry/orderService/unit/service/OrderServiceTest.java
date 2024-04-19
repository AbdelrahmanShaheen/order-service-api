package com.fawry.orderService.unit.service;

import com.fawry.orderService.RestTemplateClient;
import com.fawry.orderService.dto.notification.NotificationRequest;
import com.fawry.orderService.dto.order.OrderItemRequest;
import com.fawry.orderService.dto.order.OrderRequest;
import com.fawry.orderService.dto.order.OrderResponse;
import com.fawry.orderService.entity.Order;
import com.fawry.orderService.entity.OrderItem;
import com.fawry.orderService.mapper.OrderMapper;
import com.fawry.orderService.repository.OrderRepo;
import com.fawry.orderService.service.OrderServiceImp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepo orderRepo;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private RestTemplateClient restTemplateClient;

    @Mock
    private KafkaTemplate<String, NotificationRequest> kafkaTemplate;

    @InjectMocks
    private OrderServiceImp orderService;

    @Test
    public void createOrderShouldSuccess() {
        // Arrange
        Set<OrderItemRequest> orderItems = new HashSet<>();
        orderItems.add(new OrderItemRequest("e4d362c0-0f15-4789-a80a-e680a5f22e25", 3));
        orderItems.add(new OrderItemRequest("50a6b1b7-f004-4dfa-aa2a-45fe8bd3f8a1", 1));

        OrderRequest orderRequest = OrderRequest.builder()
                .userEmail("shaheenabdelrahman28@gmail.com")
                .couponCode("FAWRY-2024")
                .cardNumber("6494036324068254")
                .cvv("9643")
                .totalPrice(200.0)
                .totalPriceAfterDiscount(150.0)
                .orderItems(orderItems)
                .build();

        Order order = new Order();
        order.setId(1);
        order.setUserEmail(orderRequest.getUserEmail());
        order.setCouponCode(orderRequest.getCouponCode());
        order.setTotalPrice(orderRequest.getTotalPrice());
        order.setTotalPriceAfterDiscount(orderRequest.getTotalPriceAfterDiscount());
        order.setOrderItems(orderRequest.getOrderItems().stream()
                .map(item -> OrderItem.builder().productCode(item.getProductCode()).quantity(item.getQuantity()).build())
                .collect(Collectors.toSet()));

        when(orderMapper.toEntity(orderRequest)).thenReturn(order);
        when(orderRepo.save(order)).thenReturn(order);

        // Act
        orderService.createOrder(orderRequest);

        // Assert
        verify(orderMapper).toEntity(orderRequest);
        verify(orderRepo).save(order);
    }
    @Test
    public void getOrdersByCustomerAndDateRangeShouldSuccess() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().minusDays(10);
        LocalDateTime endDate = LocalDateTime.now();
        String customerEmail = "shaheenabdelrahman28@gmail.com";

        OrderItem orderItem1 = new OrderItem();
        orderItem1.setProductCode("e4d362c0-0f15-4789-a80a-e680a5f22e25");
        orderItem1.setQuantity(3);

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setProductCode("50a6b1b7-f004-4dfa-aa2a-45fe8bd3f8a1");
        orderItem2.setQuantity(1);

        Set<OrderItem> orderItems1 = new HashSet<>(Arrays.asList(orderItem1, orderItem2));

        Order order1 = new Order();
        order1.setUserEmail(customerEmail);
        order1.setCouponCode("FAWRY-2024");
        order1.setTotalPrice(200.0);
        order1.setTotalPriceAfterDiscount(150.0);
        order1.setOrderItems(orderItems1);

        Order order2 = new Order();
        order2.setUserEmail(customerEmail);
        order2.setCouponCode("FAWRY-2025");
        order2.setTotalPrice(300.0);
        order2.setTotalPriceAfterDiscount(250.0);
        order2.setOrderItems(new HashSet<>(orderItems1));

        List<Order> orders = Arrays.asList(order1, order2);

        OrderResponse orderResponse1 = new OrderResponse();
        orderResponse1.setUserEmail(order1.getUserEmail());
        orderResponse1.setCouponCode(order1.getCouponCode());
        orderResponse1.setTotalPrice(order1.getTotalPrice());
        orderResponse1.setTotalPriceAfterDiscount(order1.getTotalPriceAfterDiscount());
        orderResponse1.setCreationDate(new Date());

        OrderResponse orderResponse2 = new OrderResponse();
        orderResponse2.setUserEmail(order2.getUserEmail());
        orderResponse2.setCouponCode(order2.getCouponCode());
        orderResponse2.setTotalPrice(order2.getTotalPrice());
        orderResponse2.setTotalPriceAfterDiscount(order2.getTotalPriceAfterDiscount());
        orderResponse2.setCreationDate(new Date());

        List<OrderResponse> orderResponses = Arrays.asList(orderResponse1, orderResponse2);

        when(orderRepo.findByUserEmailAndCreationDateBetween(customerEmail, startDate, endDate)).thenReturn(orders);
        when(orderMapper.toDTO(order1)).thenReturn(orderResponse1);
        when(orderMapper.toDTO(order2)).thenReturn(orderResponse2);

        // Act
        List<OrderResponse> result = orderService.getOrdersByCustomerAndDateRange(customerEmail, startDate, endDate);

        // Assert
        assertThat(orderResponses).isEqualTo(result);
    }
}
