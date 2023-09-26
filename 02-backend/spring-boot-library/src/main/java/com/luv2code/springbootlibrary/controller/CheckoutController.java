package com.luv2code.springbootlibrary.controller;

import com.luv2code.springbootlibrary.entity.Checkout;
import com.luv2code.springbootlibrary.responsemodels.CheckoutDueResponse;
import com.luv2code.springbootlibrary.service.CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("https://localhost:3000")
@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    @Autowired
    CheckoutService checkoutService;

    @GetMapping("/secure/payment-due")
    public CheckoutDueResponse getCheckedOutBooksWithPaymentDue (@AuthenticationPrincipal Jwt principal) {
        String userEmail = principal.getClaimAsString("sub");
        return checkoutService.getCheckedOutBooksWithPaymentDue(userEmail);
    }
}
