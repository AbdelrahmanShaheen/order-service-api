package com.fawry.orderService.service;

import com.fawry.orderService.dto.*;
import com.fawry.orderService.entity.Order;
import com.fawry.orderService.mapper.OrderMapper;
import com.fawry.orderService.repository.OrderRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepo orderRepo;
    private final OrderMapper orderMapper;

    public void createOrder(OrderRequest orderRequest){
        // withdraw totalPriceAfterDiscount from customer
        WithdrawRequest withdrawRequest = new WithdrawRequest(orderRequest.getCvv() ,orderRequest.getCardNumber() ,orderRequest.getAmount());
        this.withdrawFromCustomer(withdrawRequest);
        // deposit totalPriceAfterDiscount to merchant
        DepositRequest depositRequest = new DepositRequest(orderRequest.getCardNumber() ,orderRequest.getAmount());
        this.depositToMerchant(depositRequest);
        // save the order to the db
        Date date = new Date();
        Order order = orderMapper.toEntity(orderRequest);
        order.setCreationDate(date);
        Order savedOrder = orderRepo.save(order);
        // if coupon is valid consume it
        if(orderRequest.getIsValidCoupon()){
            this.consumeCoupon(new ConsumeCouponRequest(orderRequest.getCouponCode(), order.getId(), date));
        }
        // consume products from the stock
        List<ConsumeProductRequest>consumeProductRequestList = orderRequest.getOrderItems()
                .stream().map(orderItemRequest -> ConsumeProductRequest.builder()
                        .productCode(orderItemRequest.getProductCode())
                        .orderId(savedOrder.getId())
                        .quantity(orderItemRequest.getQuantity())
                        .consumedAt(date)
                        .build())
                        .toList();
        this.consumeProductsFromStock(consumeProductRequestList);
        // notify customer and merchant about the order
        NotificationRequest customerNotification = NotificationRequest
                                                    .builder()
                                                    .destinationEmail(savedOrder.getUserEmail())
                                                    .msg(this.createCustomerNotificationMsg(savedOrder)).build();
        NotificationRequest merchantNotification = NotificationRequest
                                                    .builder()
                                                    .destinationEmail("merchant@gmail.com")
                                                    .msg(this.createMerchantNotificationMsg(savedOrder)).build();
        this.notify(customerNotification);
        this.notify(merchantNotification);
        System.out.println(savedOrder);
    }
    public void withdrawFromCustomer(WithdrawRequest withdrawRequest){
        log.info("Calling bank api to withdraw from customer");
        log.info(String.valueOf(withdrawRequest));
    }
    public void depositToMerchant(DepositRequest depositRequest){
        log.info("Calling bank api to deposit to merchant");
        log.info(String.valueOf(depositRequest));
    }
    public void consumeCoupon(ConsumeCouponRequest consumeCouponRequest){
        log.info("consume coupon!");
        log.info(String.valueOf(consumeCouponRequest));
    }
    public void consumeProductsFromStock(List<ConsumeProductRequest> consumedProducts){
        log.info("consumed products from the store");
        log.info(String.valueOf(consumedProducts));
    }

    public List<OrderResponse> getOrdersByCustomerAndDateRange(String customerEmail, LocalDateTime startDate, LocalDateTime endDate) {
        List<Order>orders = orderRepo.findByUserEmailAndCreationDateBetween(customerEmail,startDate,endDate);
        return orders.stream().map(orderMapper::toDTO).toList();
    }
    public void notify(NotificationRequest notificationRequest){
        log.info(notificationRequest.getDestinationEmail());
        log.info(notificationRequest.getMsg());
    }
    public String createCustomerNotificationMsg(Order order){
        return "Dear Customer,\n\n"
                + "Thank you for placing your order with us. Your order has been successfully placed.\n\n"
                + "Order Number: " + order.getId() + "\n"
                + "Total Price: " + order.getTotalPriceAfterDiscount() + "\n"
                + "Order Date: " + order.getCreationDate() + "\n\n"
                + "We will notify you once your order has been processed and shipped.\n"
                + "If you have any questions, please feel free to contact our customer support team.\n\n"
                + "Thank you for shopping with us!\n"
                + "Sincerely,\n";
    }
    public String createMerchantNotificationMsg(Order order){
        return "Dear Merchant,\n\n"
                + "A new order has been placed in your store.\n\n"
                + "Order Number: " + order.getId() + "\n"
                + "Total Price: " + order.getTotalPriceAfterDiscount() + "\n"
                + "Order Date: " + order.getCreationDate() + "\n\n"
                + "Please process the order and prepare it for shipment.\n\n";
    }
}
