package com.luv2code.springbootlibrary.controller;

import com.luv2code.springbootlibrary.entity.Book;
import com.luv2code.springbootlibrary.responsemodels.ShelfCurrentLoansResponse;
import com.luv2code.springbootlibrary.service.BookService;
import com.luv2code.springbootlibrary.utils.ExtractJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("https://localhost:3000")
@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/secure/currentloans")
    public List<ShelfCurrentLoansResponse> currentLoans(@AuthenticationPrincipal Jwt principal)
        throws Exception
    {
        //String userEmail = ExtractJWT.payloadJWTExtraction("token", "\"sub\"");
        String userEmail = principal.getClaimAsString("sub");
        return bookService.currentLoans(userEmail);
    }

    @GetMapping("/secure/currentloans/count")
    public int currentLoansCount(@AuthenticationPrincipal Jwt principal) {
        String userEmail = principal.getClaimAsString("sub");
        return bookService.currentLoansCount(userEmail);
    }

    @GetMapping("/secure/ischeckedout/byuser")
    public Boolean checkoutBookByUser(@AuthenticationPrincipal Jwt principal,
                                      @RequestParam Long bookId) {
        String userEmail = principal.getClaimAsString("sub");
        return bookService.checkoutBookByUser(userEmail, bookId);
    }

    @PutMapping("/secure/checkout")
    public Book checkoutBook (@AuthenticationPrincipal Jwt principal,
                              @RequestParam Long bookId) throws Exception {
        String userEmail = principal.getClaimAsString("sub");
        return bookService.checkoutBook(userEmail, bookId);
    }

    @PutMapping("/secure/return")
    public void returnBook(@AuthenticationPrincipal Jwt principal,
                           @RequestParam Long bookId) throws Exception {
        String userEmail = principal.getClaimAsString("sub");
        bookService.returnBook(userEmail, bookId);
    }

    @PutMapping("/secure/renew/loan")
    public void renewLoan(@AuthenticationPrincipal Jwt principal,
                          @RequestParam Long bookId) throws Exception {
        String userEmail = principal.getClaimAsString("sub");
        bookService.renewLoan(userEmail, bookId);
    }

}












