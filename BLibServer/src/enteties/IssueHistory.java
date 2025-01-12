package enteties;

import java.io.Serializable;

public class IssueHistory implements Serializable{
	private static final long serialVersionUID = 1L;
	private String issueType;
	private String issueDate;
	private String issueDescription;

	public IssueHistory(String issueType, String issueDate, String issueDescription) {
		this.issueType = issueType;
		this.issueDate = issueDate;
		this.issueDescription = issueDescription;
	}

	public String getIssueType() {
		return issueType;
	}

	public String getIssueDate() {
		return issueDate;
	}

	public String getIssueDescription() {
		return issueDescription;
	}
}