package com.fawry.orderService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static final String SEND_NOTIFICATION_URL = "http://localhost:8081/api/v1/notification";
    public static final String CONSUME_COUPON_URL = "http://localhost:8080/api/coupons/consume";
    public static final String CONSUME_PRODUCTS_FROM_STOCK_URL = "http://localhost:8083/api/stocks/consume";
    public static final String WITHDRAW_FROM_BANK_URL = "http://localhost:8084/bank/transaction/withdraw";
    public static final String DEPOSIT_TO_BANK_URL = "http://localhost:8084/bank/transaction/deposit";
    public static final String PRODUCT_CONSUMPTION_URL = "http://localhost:8085/api/products/buy-products";
    public static final String MERCHANT_CARD_NUMBER = "1092002294900653";
    public static final String CUSTOMER_NOTIFICATION_SUBJECT = "About Your order";
    public static final String MERCHANT_NOTIFICATION_SUBJECT = "New Order has been placed";
    public static final String MERCHANT_EMAIL = "merchant@gmail.com";

    public static String extractMessagesFromJson(String jsonResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);
            List<String> messages = new ArrayList<>();

            if (jsonNode.isArray()) {
                for (JsonNode node : jsonNode) {
                    JsonNode messageNode = node.get("message");
                    if (messageNode != null && !messageNode.isNull()) {
                        String message = messageNode.asText().replace(".", "");
                        messages.add(message);
                    }
                }
            } else {
                // Handle single object
                JsonNode messageNode = jsonNode.get("message");
                if (messageNode != null && !messageNode.isNull()) {
                    String message = messageNode.asText().replace(".", "");
                    messages.add(message);
                }
            }

            // Concatenate messages with '|'
            return String.join("|", messages);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}