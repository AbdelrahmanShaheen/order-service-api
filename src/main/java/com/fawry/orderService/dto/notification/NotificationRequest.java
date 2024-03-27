package com.fawry.orderService.dto.notification;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class NotificationRequest {
    private String destinationEmail;
    private String msg;
    private String subject;
}
