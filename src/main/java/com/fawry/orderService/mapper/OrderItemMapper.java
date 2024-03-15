package com.fawry.orderService.mapper;

import com.fawry.orderService.dto.OrderItemRequest;
import com.fawry.orderService.entity.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItem toEntity(OrderItemRequest orderItemRequest);
}
