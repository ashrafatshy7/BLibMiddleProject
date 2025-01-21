
package enteties;

import java.io.Serializable;

public class Loan implements Serializable {
	private static final long serialVersionUID = 1L;
	private String bookTitle;
	private String borrowDate;
	private String returnDate;
	private String barcode;

	public Loan(String bookTitle, String borrowDate, String returnDate) {
		this.bookTitle = bookTitle;
		this.borrowDate = borrowDate;
		this.returnDate = returnDate;
	}
	
	public Loan(String barcode, String borrowDate, String returnDate, boolean loan) {
		this.barcode = barcode;
		this.borrowDate = borrowDate;
		this.returnDate = returnDate;
	}

	public Loan(String bookTitle, String returnDate) {
		this.bookTitle = bookTitle;
		this.returnDate = returnDate;
	}
	
	
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	
	public String getSubscriberID() {
		return barcode;
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

	public void setReturnDate(String returnDate) {
		this.returnDate = returnDate;
	}

	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}
}

