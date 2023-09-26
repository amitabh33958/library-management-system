package com.luv2code.springbootlibrary.controller;

import com.luv2code.springbootlibrary.service.PaymentService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("https://localhost:3000")
@RestController
@RequestMapping("/api/webhook")
public class StripeWebHookController {

    private PaymentService paymentService;

    @Autowired
    public StripeWebHookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/stripe/events")
    public String handleStripeEvent(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) throws StripeException {

        if(sigHeader == null) {
            return "";
        }
        return paymentService.handleStripeEvent(payload, sigHeader);
    }
}
