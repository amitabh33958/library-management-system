package com.luv2code.springbootlibrary.service;

import com.luv2code.springbootlibrary.dao.BookRepository;
import com.luv2code.springbootlibrary.dao.CheckoutRepository;
import com.luv2code.springbootlibrary.dao.HistoryRepository;
import com.luv2code.springbootlibrary.dao.PaymentRepository;
import com.luv2code.springbootlibrary.entity.Book;
import com.luv2code.springbootlibrary.entity.Checkout;
import com.luv2code.springbootlibrary.entity.History;
import com.luv2code.springbootlibrary.responsemodels.ShelfCurrentLoansResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.luv2code.springbootlibrary.utils.DateUtils.calculateDifferenceInTime;


@Service
@Transactional
public class BookService {

    private BookRepository bookRepository;

    private CheckoutRepository checkoutRepository;

    private HistoryRepository historyRepository;

    public BookService(BookRepository bookRepository, CheckoutRepository checkoutRepository,
                       HistoryRepository historyRepository, PaymentRepository paymentRepository) {
        this.bookRepository = bookRepository;
        this.checkoutRepository = checkoutRepository;
        this.historyRepository = historyRepository;
    }


    public Book checkoutBook(String userEmail, Long bookId) throws Exception {

        Optional<Book> book = bookRepository.findById(bookId);
        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookIdAndBookReturned(userEmail, bookId, false);
        if (book.isEmpty() || validateCheckout != null || book.get().getCopiesAvailable() <= 0) {
            throw new Exception("Book doesn't exist or already checked out by user");
        }

        List<Checkout> currentBooksCheckedOut = checkoutRepository.findBooksByUserEmail(userEmail);
        AtomicBoolean bookNeedsReturned = new AtomicBoolean(false);
        currentBooksCheckedOut.stream()
                .filter(checkout -> !checkout.isBookReturned())
                .forEach(checkout -> {
                    Long differenceInTime = calculateDifferenceInTime(checkout.getReturnDate());
                    if (differenceInTime < 0) {
                        bookNeedsReturned.set(true);
                    }
                });
        if (bookNeedsReturned.get()) {
            throw new Exception("Outstanding fees");
        }
        book.get().setCopiesAvailable(book.get().getCopiesAvailable() - 1);
        bookRepository.save(book.get());
        Checkout checkout = new Checkout(
                userEmail,
                LocalDate.now().toString(),
                LocalDate.now().plusDays(7).toString(),
                false,
                0.00,
                book.get()
        );
        checkoutRepository.save(checkout);
        return book.get();
    }

    public Boolean checkoutBookByUser(String userEmail, Long bookId) {
        Checkout currentCheckout = checkoutRepository.findByUserEmailAndBookIdAndBookReturned(userEmail, bookId, false);
        return currentCheckout != null;
    }

    public int currentLoansCount(String userEmail) {
        return checkoutRepository.findBooksByUserEmailAndBookReturned(userEmail, false).size();
    }

    public List<ShelfCurrentLoansResponse> currentLoans(String userEmail) {
        List<Checkout> checkoutList = checkoutRepository.findBooksByUserEmail(userEmail);
        return checkoutList.stream()
                .filter(checkout -> !checkout.isBookReturned())
                .map(checkout -> {
                    Long differenceInTime = calculateDifferenceInTime(checkout.getReturnDate());
                    if (differenceInTime < 0) {
                        checkout.setDueAmount(differenceInTime * -10);
                        checkoutRepository.save(checkout);
                    }
                    return new ShelfCurrentLoansResponse(checkout.getBook(), Math.toIntExact(differenceInTime));
                }).collect(Collectors.toList());
    }

    public void returnBook(String userEmail, Long bookId) throws Exception {

        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookIdAndBookReturned(userEmail, bookId, false);
        if (validateCheckout == null || validateCheckout.getBook() == null) {
            throw new Exception("Book does not exist or not checked out by user");
        }
        if (validateCheckout.getDueAmount() > 0) {
            throw new Exception("Outstanding dues for the book. Cannot return without payment");
        }
        Book checkedOutBook = validateCheckout.getBook();
        checkedOutBook.setCopiesAvailable(checkedOutBook.getCopiesAvailable() + 1);

        bookRepository.save(checkedOutBook);
        Long differenceInTime = calculateDifferenceInTime(validateCheckout.getReturnDate());
        if (differenceInTime < 0) {
            validateCheckout.setDueAmount(differenceInTime * -10);
        }
        validateCheckout.setBookReturned(true);
        validateCheckout.setActualReturnDate(LocalDate.now().toString());
        checkoutRepository.save(validateCheckout);
        History history = new History(
                userEmail,
                validateCheckout.getCheckoutDate(),
                LocalDate.now().toString(),
                checkedOutBook.getTitle(),
                checkedOutBook.getAuthor(),
                checkedOutBook.getDescription(),
                checkedOutBook.getImg()
        );
        historyRepository.save(history);
    }

    public void renewLoan(String userEmail, Long bookId) throws Exception {

        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookIdAndBookReturned(userEmail, bookId, false);
        if (validateCheckout == null) {
            throw new Exception("Book does not exist or not checked out by user");
        }
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = sdFormat.parse(validateCheckout.getReturnDate());
        Date d2 = sdFormat.parse(LocalDate.now().toString());

        List<Checkout> currentBooksCheckedOut = checkoutRepository.findBooksByUserEmail(userEmail);
        AtomicBoolean bookNeedsReturned = new AtomicBoolean(false);
        currentBooksCheckedOut.stream()
                .filter(checkout -> !checkout.isBookReturned())
                .forEach(checkout -> {
                    Long differenceInTime = calculateDifferenceInTime(checkout.getReturnDate());
                    if (differenceInTime < 0) {
                        bookNeedsReturned.set(true);
                    }
                });
        if (bookNeedsReturned.get()) {
            throw new Exception("Outstanding fees");
        }
        if (d1.compareTo(d2) > 0 || d1.compareTo(d2) == 0) {
            validateCheckout.setReturnDate(LocalDate.now().plusDays(7).toString());
            checkoutRepository.save(validateCheckout);
        }
    }

}















