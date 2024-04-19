package com.fawry.orderService.unit.repository;

import com.fawry.orderService.OrderServiceApplication;
import com.fawry.orderService.entity.Order;
import com.fawry.orderService.repository.OrderRepo;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)

public class OrderRepositoryTest {
    @Autowired
    private OrderRepo orderRepo;
    @Test
    @Sql(statements = "INSERT INTO \"order\" (id, order_code, coupon_code, user_email, total_price, total_price_after_discount, creation_date) VALUES\n" +
            "(1, 'order1', 'coupon1', 'test1@test.com', 100.0, 90.0, '2022-01-01 00:00:00'),\n" +
            "(2, 'order2', 'coupon2', 'test2@test.com', 200.0, 180.0, '2022-01-02 00:00:00'),\n" +
            "(3, 'order3', 'coupon3', 'test1@test.com', 300.0, 270.0, '2022-01-03 00:00:00');\n" +
            "\n")
    public void testFindByUserEmailAndCreationDateBetween(){
        LocalDateTime startDate = LocalDateTime.parse("2022-01-01T00:00:00");
        LocalDateTime endDate = LocalDateTime.parse("2022-01-03T00:00:00");
        List<Order> result = orderRepo.findByUserEmailAndCreationDateBetween("test1@test.com", startDate, endDate);
        assertEquals(2, result.size());
    }
}
