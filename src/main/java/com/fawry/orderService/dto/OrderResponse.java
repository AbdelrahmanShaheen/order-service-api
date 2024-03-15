package com.fawry.orderService.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OrderResponse {
    private String couponCode;
    private String userEmail;
    private Double totalPrice;
    private Double totalPriceAfterDiscount;
    private Date creationDate;
}
