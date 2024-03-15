package com.fawry.orderService.repository;

import com.fawry.orderService.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepo extends JpaRepository<Order ,Integer> {
      List<Order> findByUserEmailAndCreationDateBetween(String customerEmail, LocalDateTime startDate, LocalDateTime endDate);
}
