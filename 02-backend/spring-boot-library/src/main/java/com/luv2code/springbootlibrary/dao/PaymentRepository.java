package com.luv2code.springbootlibrary.dao;

import com.luv2code.springbootlibrary.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentHistory, Long> {

    PaymentHistory findByUserEmail(String userEmail);
}
