package com.fawry.orderService.exception;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
@Data
public class ErrorResponse{
    private String timestamp;

    /** HTTP Status Code */
    private int status;

    /** A message that describe the error thrown when calling the downstream API */
    private String message;


    public ErrorResponse(int status ,String message) {
        this.timestamp = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now());
        this.status = status;
        this.message = message;
    }
}
