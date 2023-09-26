package com.luv2code.springbootlibrary.responsemodels;

import com.luv2code.springbootlibrary.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckoutDue {

    private String userEmail;

    private String checkoutDate;

    private String returnDate;

    private double dueAmount;

    private int daysPastDue;

    private Book book;
}
