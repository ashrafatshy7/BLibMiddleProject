package enteties;

import java.time.LocalDate;

public class LoanHistory {
	private String bookTitle;
	private String loanDate;
	private String dueDate;
	private boolean returned; // Indicates whether the book has been returned

	// Constructor
	public LoanHistory(String bookTitle, String loanDate, String dueDate, boolean returned) {
		this.bookTitle = bookTitle;
		this.loanDate = loanDate;
		this.dueDate = dueDate;
		this.returned = returned;
	}

	// Getters and setters
	public String getBookTitle() {
		return bookTitle;
	}

	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}

	public String getLoanDate() {
		return loanDate;
	}

	public void setLoanDate(String loanDate) {
		this.loanDate = loanDate;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public boolean isReturned() {
		return returned;
	}

	public void setReturned(boolean returned) {
		this.returned = returned;
	}

	@Override
	public String toString() {
		return "LoanHistory [bookTitle=" + bookTitle + ", loanDate=" + loanDate + ", dueDate=" + dueDate + ", returned="
				+ returned + "]";
	}
}
