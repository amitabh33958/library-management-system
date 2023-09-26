package com.luv2code.springbootlibrary.service;

import com.luv2code.springbootlibrary.dao.CheckoutRepository;
import com.luv2code.springbootlibrary.entity.Checkout;
import com.luv2code.springbootlibrary.responsemodels.CheckoutDue;
import com.luv2code.springbootlibrary.responsemodels.CheckoutDueResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.luv2code.springbootlibrary.utils.DateUtils.calculateDifferenceInTime;

@Service
public class CheckoutService {

    @Autowired
    CheckoutRepository checkoutRepository;

    public CheckoutDueResponse getCheckedOutBooksWithPaymentDue(String userEmail) {
        List<Checkout> checkoutList = checkoutRepository.findBooksByUserEmail(userEmail);
        final double[] totalDueAmount = {0.0};

        List<CheckoutDue> checkoutDueList = checkoutList.stream()
                .filter(checkout -> checkout.getDueAmount() > 0)
                .map(checkout -> {
                    Long differenceInTime = calculateDifferenceInTime(checkout.getReturnDate());
                    totalDueAmount[0] += checkout.getDueAmount();
                    return new CheckoutDue(checkout.getUserEmail(), checkout.getCheckoutDate(), checkout.getReturnDate(),
                            checkout.getDueAmount(), Math.toIntExact(Math.abs(differenceInTime)), checkout.getBook());
                })
                .toList();
        return new CheckoutDueResponse(checkoutDueList, totalDueAmount[0]);
    }
}
