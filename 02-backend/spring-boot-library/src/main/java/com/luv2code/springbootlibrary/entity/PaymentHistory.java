package com.luv2code.springbootlibrary.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = ("payment_history"))
@Data
@Builder
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "amount")
    private double amount;

    @Column(name = "payment_gateway")
    private String paymentGateway;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "currency")
    private String currency;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "country")
    private String country;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "payment_status")
    private String paymentStatus;
}
