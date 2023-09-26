package com.luv2code.springbootlibrary.service;

import com.luv2code.springbootlibrary.dao.CheckoutRepository;
import com.luv2code.springbootlibrary.dao.PaymentRepository;
import com.luv2code.springbootlibrary.entity.Checkout;
import com.luv2code.springbootlibrary.entity.PaymentHistory;
import com.luv2code.springbootlibrary.requestmodels.PaymentInfoRequest;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionRetrieveParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.stripe.param.checkout.SessionCreateParams.Mode;
import static com.stripe.param.checkout.SessionCreateParams.builder;

@Service
@Transactional
public class PaymentService {

    private PaymentRepository paymentRepository;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @Autowired
    CheckoutRepository checkoutRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, @Value("${stripe.key.secret}") String secretKey) {
        this.paymentRepository = paymentRepository;
        Stripe.apiKey = secretKey;
    }

    public PaymentIntent createPaymentIntent(PaymentInfoRequest paymentInfoRequest) throws StripeException {
        List<String> paymentMethodTypes = new ArrayList<>();
        paymentMethodTypes.add("card");

        Map<String, Object> params = new HashMap<>();
        params.put("amount", paymentInfoRequest.getAmount());
        params.put("currency", paymentInfoRequest.getCurrency());
        params.put("payment_method_types", paymentMethodTypes);

        return PaymentIntent.create(params);
    }

    public Session createCheckoutSession(String userEmail) throws StripeException {
        List<Checkout> checkoutList = checkoutRepository.findBooksByUserEmail(userEmail);
        double totalDueAmount = checkoutList.stream().mapToDouble(Checkout::getDueAmount).sum();

        SessionCreateParams params =
                builder()
                        .setMode(Mode.PAYMENT)
                        .setSuccessUrl("https://localhost:3000/payment-success")
                        .setCancelUrl("https://localhost:3000/payment-cancel")
                        //      .addPaymentMethodType(PaymentMethodType.CARD)
                        .setCurrency("inr")
                        .setCustomerEmail(userEmail)
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName("Library Dues")
                                                        .setDescription("Dues for late return of Books issued")
                                                        .build())
                                                .setUnitAmount((long) totalDueAmount * 100)
                                                .setCurrency("inr")
                                                .build())
                                        .build())
                        .build();
        return Session.create(params);
    }

    public ResponseEntity<String> stripePayment(String userEmail) throws Exception {
        PaymentHistory payment = paymentRepository.findByUserEmail(userEmail);

        if (payment == null) {
            throw new Exception("Payment information is missing");
        }
        payment.setAmount(00.00);
        paymentRepository.save(payment);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    public String handleStripeEvent(String payload, String sigHeader) throws StripeException {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            // Invalid signature
            System.out.println("⚠️  Webhook error while validating signature.");
            return null;
        }

        if (event.getType().equals("checkout.session.completed")) {
            Session sessionEvent = (Session) event.getDataObjectDeserializer().getObject().get();
            SessionRetrieveParams params =
                    SessionRetrieveParams.builder()
                            .addExpand("line_items")
                            .build();

            Session session = Session.retrieve(sessionEvent.getId(), params, null);
            // Fulfill the purchase...
            fulfillPayment(session);
        } else {
            System.out.println("Unhandled event type: " + event.getType());
        }
        return "";
    }

    private void fulfillPayment(Session session) {
        Instant instant = Instant.ofEpochSecond(session.getCreated());
        PaymentHistory paymentHistory = PaymentHistory.builder()
                .paymentId(session.getPaymentIntent())
                .userEmail(session.getCustomerEmail())
                .amount((double) session.getAmountTotal() / 100)
                .paymentGateway("Stripe")
                .currency(session.getCurrency().toUpperCase())
                .paymentDate(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()))
                .country(session.getCustomerDetails().getAddress().getCountry())
                .customerName(session.getCustomerDetails().getName())
                .paymentMethod(session.getPaymentMethodTypes().get(0).toUpperCase())
                .paymentStatus(session.getPaymentStatus())
                .build();
        paymentRepository.save(paymentHistory);

        List<Checkout> checkoutList = checkoutRepository.findBooksByUserEmail(paymentHistory.getUserEmail());
        checkoutList.stream()
                .filter(checkout -> checkout.getDueAmount() > 0)
                .forEach(checkout -> {
                    checkout.setDueAmount(0.00);
                    checkout.setBookReturned(true);
                    checkout.setActualReturnDate(LocalDate.now().toString());
                    checkoutRepository.save(checkout);
                });
    }
}










