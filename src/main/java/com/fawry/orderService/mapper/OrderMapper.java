package com.fawry.orderService.mapper;

import com.fawry.orderService.dto.OrderRequest;
import com.fawry.orderService.dto.OrderResponse;
import com.fawry.orderService.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Date;


@Mapper(componentModel = "spring", imports = Date.class)
public interface OrderMapper {
    Order toEntity(OrderRequest orderRequest);
    OrderResponse toDTO(Order order);
}
