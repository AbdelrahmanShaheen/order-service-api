package com.fawry.orderService.mapper;

import com.fawry.orderService.dto.order.OrderRequest;
import com.fawry.orderService.dto.order.OrderResponse;
import com.fawry.orderService.entity.Order;
import org.mapstruct.Mapper;

import java.util.Date;


@Mapper(componentModel = "spring", imports = Date.class)
public interface OrderMapper {
    Order toEntity(OrderRequest orderRequest);
    OrderResponse toDTO(Order order);
}
