package com.fawry.orderService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class WithdrawRequest {
  private Integer cvv;
  private String cardNumber;
  private Double amount;
}
