package com.fawry.orderService.service;

import com.fawry.orderService.RestTemplateClient;
import com.fawry.orderService.dto.coupon.ConsumeCouponRequest;
import com.fawry.orderService.dto.notification.NotificationRequest;
import com.fawry.orderService.dto.order.OrderRequest;
import com.fawry.orderService.dto.order.OrderResponse;
import com.fawry.orderService.dto.product.ProductConsumptionRequest;
import com.fawry.orderService.dto.stock.ConsumeProductRequest;
import com.fawry.orderService.dto.transaction.DepositRequest;
import com.fawry.orderService.dto.transaction.WithdrawRequest;
import com.fawry.orderService.entity.Order;
import com.fawry.orderService.mapper.OrderMapper;
import com.fawry.orderService.repository.OrderRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.fawry.orderService.Utils.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImp implements OrderService{

    private final OrderRepo orderRepo;
    private final OrderMapper orderMapper;
    private final RestTemplateClient restTemplateClient;
    private final KafkaTemplate<String , NotificationRequest> kafkaTemplate;

    public void createOrder(OrderRequest orderRequest){
        log.info(orderRequest.toString());

        Order order = orderMapper.toEntity(orderRequest);
        order.setOrderCode(UUID.randomUUID().toString());

        this.consumeProductsFromStock(orderRequest);

        this.addToProductConsumptionHistory(orderRequest ,order.getOrderCode());

        if(orderRequest.getCouponCode() != null){
            this.consumeCoupon(new ConsumeCouponRequest(orderRequest.getCouponCode(), order.getOrderCode()));
        }

        this.withdrawFromCustomer(orderRequest);

        this.depositToMerchant(orderRequest);

        order.setCreationDate(new Date());
        Order savedOrder = orderRepo.save(order);

        this.notify(savedOrder);
        log.info("Order created successfully {}",savedOrder);
    }

    private void addToProductConsumptionHistory(OrderRequest orderRequest, String orderCode) {
        log.info("Calling product api to update product consumption history");
        Date date = new Date();
        List<ProductConsumptionRequest>productConsumptionRequestList = orderRequest.getOrderItems()
                .stream().map(orderItemRequest -> ProductConsumptionRequest.builder()
                        .productCode(orderItemRequest.getProductCode())
                        .orderCode(orderCode)
                        .quantityConsumed(orderItemRequest.getQuantity())
                        .consumedAt(date)
                        .build())
                .toList();
        log.info("Consumed products {}",String.valueOf(productConsumptionRequestList));
        restTemplateClient.addToProductConsumptionHistory(productConsumptionRequestList);
    }

    public List<OrderResponse> getOrdersByCustomerAndDateRange(String customerEmail, LocalDateTime startDate, LocalDateTime endDate) {
        List<Order>orders = orderRepo.findByUserEmailAndCreationDateBetween(customerEmail,startDate,endDate);
        return orders.stream().map(orderMapper::toDTO).toList();
    }

    private void withdrawFromCustomer(OrderRequest orderRequest){
        log.info("Calling bank api to withdraw from customer");
        WithdrawRequest withdrawRequest = WithdrawRequest.builder()
                                                .cardNumber(orderRequest.getCardNumber())
                                                .CVV(orderRequest.getCvv())
                                                .amount(orderRequest.getTotalPriceAfterDiscount())
                                                .build();
        restTemplateClient.withdraw(withdrawRequest);
        log.info(String.valueOf("Withdraw request {}"),withdrawRequest);
    }

    private void depositToMerchant(OrderRequest orderRequest){
        log.info("Calling bank api to deposit to merchant");
        DepositRequest depositRequest = DepositRequest.builder()
                                                .cardNumber(MERCHANT_CARD_NUMBER)
                                                .amount(orderRequest.getTotalPriceAfterDiscount())
                                                .build();
        restTemplateClient.deposit(depositRequest);
        log.info("Deposit request {}",String.valueOf(depositRequest));
    }

    private void consumeCoupon(ConsumeCouponRequest consumeCouponRequest){
        log.info("Calling coupon api to consume the coupon");
        log.info("Consumed coupon {}",String.valueOf(consumeCouponRequest));
        restTemplateClient.consumeCoupon(consumeCouponRequest);
    }

    private void consumeProductsFromStock(OrderRequest orderRequest){
        List<ConsumeProductRequest>consumeProductRequestList = orderRequest.getOrderItems()
                .stream().map(orderItemRequest -> ConsumeProductRequest.builder()
                        .productCode(orderItemRequest.getProductCode())
                        .quantity(orderItemRequest.getQuantity())
                        .build())
                .toList();
        log.info("consumed products {} from the store" ,consumeProductRequestList);
        restTemplateClient.consumeProductsFromStock(consumeProductRequestList);
    }

    private void notify(Order order){
        NotificationRequest customerNotification = NotificationRequest
                .builder()
                .destinationEmail(order.getUserEmail())
                .subject(CUSTOMER_NOTIFICATION_SUBJECT)
                .msg(this.createCustomerNotificationMsg(order)).build();
        NotificationRequest merchantNotification = NotificationRequest
                .builder()
                .destinationEmail(MERCHANT_EMAIL)
                .subject(MERCHANT_NOTIFICATION_SUBJECT)
                .msg(this.createMerchantNotificationMsg(order)).build();

        kafkaTemplate.send("notificationTopic",customerNotification);
        kafkaTemplate.send("notificationTopic",merchantNotification);
        log.info("Customer Notification {}",customerNotification);
        log.info("Merchant Notification {}",merchantNotification);
    }

    private String createCustomerNotificationMsg(Order order){
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

    private String createMerchantNotificationMsg(Order order){
        return "Dear Merchant,\n\n"
                + "A new order has been placed in your store.\n\n"
                + "Order Number: " + order.getId() + "\n"
                + "Total Price: " + order.getTotalPriceAfterDiscount() + "\n"
                + "Order Date: " + order.getCreationDate() + "\n\n"
                + "Please process the order and prepare it for shipment.\n\n";
    }
}
