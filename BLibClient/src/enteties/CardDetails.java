package enteties;

import javafx.collections.ObservableList;

public class CardDetails {
	private String userName;
	private String phoneNumber;
	private ObservableList<LoanHistory> loanHistory;
	private ObservableList<IssueHistory> issuesHistory;

	// Getters and setters for the fields
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public ObservableList<LoanHistory> getLoanHistory() {
		return loanHistory;
	}

	public void setLoanHistory(ObservableList<LoanHistory> loanHistory) {
		this.loanHistory = loanHistory;
	}

	public ObservableList<IssueHistory> getIssuesHistory() {
		return issuesHistory;
	}

	public void setIssuesHistory(ObservableList<IssueHistory> issuesHistory) {
		this.issuesHistory = issuesHistory;
	}
}
