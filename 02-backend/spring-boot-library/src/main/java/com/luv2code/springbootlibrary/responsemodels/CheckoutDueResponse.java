package com.luv2code.springbootlibrary.responsemodels;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CheckoutDueResponse {

    private List<CheckoutDue> checkoutDueList;

    private double totalDueAmount;

}

