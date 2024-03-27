package com.fawry.orderService.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @NotEmpty(message = "Order must have customer email")
    @Email(message = "Customer email must be valid")
    private String userEmail;
    private String couponCode;
    private Boolean isValidCoupon;
    @NotNull
    private Double totalPrice;
    @NotNull
    private Double totalPriceAfterDiscount;
    @NotEmpty
    private String cvv;
    @NotEmpty
    private String cardNumber;
    //could be removed from the schema as it is equal to 'totalPriceAfterDiscount'
    @NotNull
    private Double amount;
    @NotEmpty
    @Valid
    private Set<OrderItemRequest> orderItems;
}
