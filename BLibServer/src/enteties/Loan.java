
package enteties;

import java.io.Serializable;
/**
 * Represents a loan record for a book in the library system.
 * Implements Serializable for object serialization.
 */
public class Loan implements Serializable {
    private static final long serialVersionUID = 1L;

    /** The title of the borrowed book. */
    private String bookTitle;

    /** The date when the book was borrowed. */
    private String borrowDate;

    /** The expected return date of the book. */
    private String returnDate;

    /** The barcode of the book. */
    private String barcode;

    /**
     * Constructs a Loan with book title, borrow date, and return date.
     *
     * @param bookTitle The title of the book.
     * @param borrowDate The date the book was borrowed.
     * @param returnDate The expected return date.
     */
    public Loan(String bookTitle, String borrowDate, String returnDate) {
        this.bookTitle = bookTitle;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
    }

    /**
     * Constructs a Loan with barcode, borrow date, and return date.
     *
     * @param barcode The barcode of the book.
     * @param borrowDate The date the book was borrowed.
     * @param returnDate The expected return date.
     * @param loan Indicates if the book is on loan.
     */
    public Loan(String barcode, String borrowDate, String returnDate, boolean loan) {
        this.barcode = barcode;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
    }

    /**
     * Constructs a Loan with book title and return date.
     *
     * @param bookTitle The title of the book.
     * @param returnDate The expected return date.
     */
    public Loan(String bookTitle, String returnDate) {
        this.bookTitle = bookTitle;
        this.returnDate = returnDate;
    }

    /**
     * Retrieves the book's barcode.
     *
     * @return The barcode of the book.
     */
    public String getBarcode() {
        return barcode;
    }

    /**
     * Sets the barcode of the book.
     *
     * @param barcode The barcode to set.
     */
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    /**
     * Retrieves the subscriber's ID (barcode used as ID).
     *
     * @return The subscriber ID.
     */
    public String getSubscriberID() {
        return barcode;
    }

    /**
     * Retrieves the book title.
     *
     * @return The book title.
     */
    public String getBookTitle() {
        return bookTitle;
    }

    /**
     * Sets the book title.
     *
     * @param bookTitle The title to set.
     */
    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    /**
     * Retrieves the borrow date.
     *
     * @return The borrow date.
     */
    public String getBorrowDate() {
        return borrowDate;
    }

    /**
     * Retrieves the return date.
     *
     * @return The return date.
     */
    public String getReturnDate() {
        return returnDate;
    }

    /**
     * Sets the return date.
     *
     * @param returnDate The return date to set.
     */
    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }
}
