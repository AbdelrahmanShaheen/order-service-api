package com.fawry.orderService;

import com.fawry.orderService.dto.ConsumeCouponRequest;
import com.fawry.orderService.dto.ConsumeProductRequest;
import com.fawry.orderService.dto.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
@Slf4j
@Component
public class RestTemplateClient {

    private final RestTemplate restTemplate;

    public RestTemplateClient(RestTemplateBuilder builder){
        this.restTemplate = builder.build();
    }
    private final String SEND_NOTIFICATION_URL = "http://localhost:8081/api/v1/notification";
    private final String CONSUME_COUPON_URL = "http://localhost:8080/api/coupons/consume";
    private final String CONSUME_PRODUCTS_FROM_STOCK_URL = "http://localhost:8080/api/stocks/consume";
    public void send(NotificationRequest notificationRequest){
        restTemplate.postForObject(SEND_NOTIFICATION_URL,notificationRequest,String.class);
    }

    public void consumeCoupon(ConsumeCouponRequest consumeCouponRequest) {
        try{
            restTemplate.postForObject(CONSUME_COUPON_URL ,consumeCouponRequest,String.class);
        }catch (RestClientResponseException ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Not a valid coupon!");
        }
    }

    public void consumeProductsFromStock(List<ConsumeProductRequest> consumedProducts) {
        try{
            String response = restTemplate.postForObject(CONSUME_PRODUCTS_FROM_STOCK_URL ,consumedProducts,String.class);
            log.info(response);
        }catch (RestClientResponseException ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Products not valid to consume");
        }
    }
}
