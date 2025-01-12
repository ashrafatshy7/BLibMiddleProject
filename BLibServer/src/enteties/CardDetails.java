package enteties;

import java.io.Serializable;

import javafx.collections.ObservableList;

public class CardDetails implements Serializable{
	private static final long serialVersionUID = 1L;
	private String subscriberCardId;

	public CardDetails(String subscriberCardId, String userName, String phoneNumber, ObservableList<Loan> loanHistory,
			ObservableList<IssueHistory> issuesHistory) {
		super();
		this.subscriberCardId = subscriberCardId;
		this.userName = userName;
		this.phoneNumber = phoneNumber;
		this.loanHistory = loanHistory;
		this.issuesHistory = issuesHistory;
	}

	public String getSubscriberCardId() {
		return subscriberCardId;
	}

	public void setSubscriberCardId(String subscriberCardId) {
		this.subscriberCardId = subscriberCardId;
	}

	private String userName;
	private String phoneNumber;
	private ObservableList<Loan> loanHistory;
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

	public ObservableList<Loan> getLoanHistory() {
		return loanHistory;
	}

	public void setLoanHistory(ObservableList<Loan> loanHistory) {
		this.loanHistory = loanHistory;
	}

	public ObservableList<IssueHistory> getIssuesHistory() {
		return issuesHistory;
	}

	public void setIssuesHistory(ObservableList<IssueHistory> issuesHistory) {
		this.issuesHistory = issuesHistory;
	}
}
