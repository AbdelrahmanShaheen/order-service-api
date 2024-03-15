package com.fawry.orderService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OrderRequest {
    private String userEmail;
    private String couponCode;
    private Boolean isValidCoupon;
    private Double totalPrice;
    private Double totalPriceAfterDiscount;
    private Integer cvv;
    private String cardNumber;
    private Double amount;
    private Set<OrderItemRequest> orderItems;
}
