package com.fawry.orderService.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductConsumptionRequest {
    private String  productCode;
    private String orderCode;
    private int quantityConsumed;
    Date consumedAt;
}
