import BookModel from "./BookModel";

class CheckoutModel {

    userEmail: string;
    checkoutDate: string;
    returnDate: string;
    dueAmount: number;
    daysPastDue: number;
    book: BookModel;

    constructor(userEmail: string, checkoutDate: string, returnDate: string, dueAmount: number, 
        daysPastDue: number, book: BookModel) {
            this.userEmail = userEmail;
            this.checkoutDate = checkoutDate;
            this.returnDate = returnDate;
            this.dueAmount = dueAmount;
            this.daysPastDue = daysPastDue;
            this.book = book;
    }
}

export default CheckoutModel;