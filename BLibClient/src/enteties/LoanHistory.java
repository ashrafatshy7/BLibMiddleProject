package enteties;

import java.time.LocalDate;

public class LoanHistory {
	private String bookTitle;
	private String borrowDate;
	private String returnDate;

	public LoanHistory(String bookTitle, String borrowDate, String returnDate) {
		this.bookTitle = bookTitle;
		this.borrowDate = borrowDate;
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
