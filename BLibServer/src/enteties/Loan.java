package enteties;

import java.io.Serializable;
import java.time.LocalDate;

public class Loan implements Serializable {
	private static final long serialVersionUID = 1L;
	private String bookTitle;
	private String borrowDate;
	private String returnDate;

	public Loan(String bookTitle, String borrowDate, String returnDate) {
		this.bookTitle = bookTitle;
		this.borrowDate = borrowDate;
		this.returnDate = returnDate;
	}

	public Loan(String bookTitle, String returnDate) {
		this.bookTitle = bookTitle;
		this.returnDate = returnDate;
	}

	public String getBookTitle() {
		return bookTitle;
	}

	public String getBorrowDate() {
		return borrowDate;
	}

	public String getReturnDate() {
		return returnDate;
	}
}
