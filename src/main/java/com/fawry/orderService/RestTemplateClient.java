package com.fawry.orderService;

import com.fawry.orderService.dto.coupon.ConsumeCouponRequest;
import com.fawry.orderService.dto.notification.NotificationRequest;
import com.fawry.orderService.dto.product.ProductConsumptionRequest;
import com.fawry.orderService.dto.stock.ConsumeProductRequest;
import com.fawry.orderService.dto.transaction.DepositRequest;
import com.fawry.orderService.dto.transaction.WithdrawRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.fawry.orderService.Utils.*;

@Slf4j
@Component
public class RestTemplateClient {

    private final RestTemplate restTemplate;

    public RestTemplateClient(RestTemplateBuilder builder){
        this.restTemplate = builder.build();
    }

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

    public void withdraw(WithdrawRequest withdrawRequest) {
        try{
            restTemplate.postForObject(WITHDRAW_FROM_BANK_URL ,withdrawRequest,String.class);
        }catch (HttpClientErrorException ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,extractMessagesFromJson(ex.getResponseBodyAsString()));
        }
    }

    public void deposit(DepositRequest depositRequest) {
        restTemplate.postForObject(DEPOSIT_TO_BANK_URL ,depositRequest ,String.class);
    }

    public void addToProductConsumptionHistory(List<ProductConsumptionRequest> productConsumptionRequestList) {
        restTemplate.postForObject(PRODUCT_CONSUMPTION_URL ,productConsumptionRequestList ,String.class);
    }
}
