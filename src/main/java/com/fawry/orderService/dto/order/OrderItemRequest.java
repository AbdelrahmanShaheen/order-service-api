package com.fawry.orderService.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data

public class OrderItemRequest {
    @NotEmpty
    private String productCode;
    @NotNull
    @Min(value = 1, message = "Product quantity must be greater than 0")
    private Integer quantity;
}
