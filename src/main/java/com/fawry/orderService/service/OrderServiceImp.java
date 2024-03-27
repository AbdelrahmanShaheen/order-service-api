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
        Order order = orderMapper.toEntity(orderRequest);
        order.setOrderCode(UUID.randomUUID().toString());
        // consume products from the stock (validate product in stock in FE)
        List<ConsumeProductRequest>consumeProductRequestList = orderRequest.getOrderItems()
                .stream().map(orderItemRequest -> ConsumeProductRequest.builder()
                        .productCode(orderItemRequest.getProductCode())
                        .quantity(orderItemRequest.getQuantity())
                        .build())
                        .toList();
        this.consumeProductsFromStock(consumeProductRequestList);
        // add consumed products to product consumption history
        Date date = new Date();
        List<ProductConsumptionRequest>productConsumptionRequestList = orderRequest.getOrderItems()
                .stream().map(orderItemRequest -> ProductConsumptionRequest.builder()
                        .productCode(orderItemRequest.getProductCode())
                        .orderCode(order.getOrderCode())
                        .quantityConsumed(orderItemRequest.getQuantity())
                        .consumedAt(date)
                        .build())
                        .toList();
        this.addToProductConsumptionHistory(productConsumptionRequestList);
        // consume coupon if exist (applying the coupon is in FE)
        this.consumeCoupon(new ConsumeCouponRequest(orderRequest.getCouponCode(), order.getOrderCode()));
        // withdraw totalPriceAfterDiscount from customer
        WithdrawRequest withdrawRequest = new WithdrawRequest(orderRequest.getCvv() ,orderRequest.getCardNumber() ,orderRequest.getTotalPriceAfterDiscount());
        this.withdrawFromCustomer(withdrawRequest);
        // deposit totalPriceAfterDiscount to merchant
        DepositRequest depositRequest = new DepositRequest(MERCHANT_CARD_NUMBER ,orderRequest.getTotalPriceAfterDiscount());
        this.depositToMerchant(depositRequest);
        // save order to the db
        order.setCreationDate(date);
        Order savedOrder = orderRepo.save(order);
        // notify customer and merchant about the order
        NotificationRequest customerNotification = NotificationRequest
                                                    .builder()
                                                    .destinationEmail(savedOrder.getUserEmail())
                                                    .subject(CUSTOMER_NOTIFICATION_SUBJECT)
                                                    .msg(this.createCustomerNotificationMsg(savedOrder)).build();
        NotificationRequest merchantNotification = NotificationRequest
                                                    .builder()
                                                    .destinationEmail(MERCHANT_EMAIL)
                                                    .subject(MERCHANT_NOTIFICATION_SUBJECT)
                                                    .msg(this.createMerchantNotificationMsg(savedOrder)).build();
        this.notify(customerNotification);
        this.notify(merchantNotification);
        System.out.println(savedOrder);
    }

    public List<OrderResponse> getOrdersByCustomerAndDateRange(String customerEmail, LocalDateTime startDate, LocalDateTime endDate) {
        List<Order>orders = orderRepo.findByUserEmailAndCreationDateBetween(customerEmail,startDate,endDate);
        return orders.stream().map(orderMapper::toDTO).toList();
    }

    private void addToProductConsumptionHistory(List<ProductConsumptionRequest>productConsumptionRequestList){
        log.info("Calling product api to update product consumption history");
        log.info(String.valueOf(productConsumptionRequestList));
        restTemplateClient.addToProductConsumptionHistory(productConsumptionRequestList);
    }
    private void withdrawFromCustomer(WithdrawRequest withdrawRequest){
        log.info("Calling bank api to withdraw from customer");
        log.info(String.valueOf(withdrawRequest));
        restTemplateClient.withdraw(withdrawRequest);
    }
    private void depositToMerchant(DepositRequest depositRequest){
        log.info("Calling bank api to deposit to merchant");
        log.info(String.valueOf(depositRequest));
        restTemplateClient.deposit(depositRequest);
    }
    private void consumeCoupon(ConsumeCouponRequest consumeCouponRequest){
        log.info("consume coupon!");
        log.info(String.valueOf(consumeCouponRequest));
        restTemplateClient.consumeCoupon(consumeCouponRequest);
    }
    private void consumeProductsFromStock(List<ConsumeProductRequest> consumedProducts){
        log.info("consumed products from the store");
        log.info(String.valueOf(consumedProducts));
        restTemplateClient.consumeProductsFromStock(consumedProducts);
    }

    private void notify(NotificationRequest notificationRequest){
        log.info(notificationRequest.getDestinationEmail());
        log.info(notificationRequest.getMsg());
//        restTemplateClient.send(notificationRequest);
        kafkaTemplate.send("notificationTopic",notificationRequest);
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
