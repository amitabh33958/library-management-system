package com.luv2code.springbootlibrary.dao;

import com.luv2code.springbootlibrary.entity.Checkout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CheckoutRepository extends JpaRepository<Checkout, Long> {

    Checkout findByUserEmailAndBookIdAndBookReturned(String userEmail, Long bookId, boolean bookReturned);

    List<Checkout> findBooksByUserEmail(String userEmail);

    List<Checkout> findBooksByUserEmailAndBookReturned(String userEmail, boolean bookReturned);

    @Modifying
    @Query("delete from Checkout where book_id in :book_id")
    void deleteAllByBookId(@Param("book_id") Long bookId);
}
