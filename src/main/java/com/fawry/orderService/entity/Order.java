package com.fawry.orderService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "\"order\"")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "order_code")
    private String orderCode;
    @Column(name = "coupon_code")
    private String couponCode;
    @Column(name = "user_email")
    private String userEmail;
    @Column(name = "total_price")
    private Double totalPrice;
    @Column(name = "total_price_after_discount")
    private Double totalPriceAfterDiscount;
    @Column(name = "creation_date")
    private Date creationDate;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id" ,referencedColumnName = "id")
    private Set<OrderItem> orderItems = new HashSet<>();

}
