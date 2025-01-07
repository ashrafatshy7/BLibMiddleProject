package enteties;

public class IssueHistory {
	private String issueType; // E.g., "Late return", "Damaged book", "Fine"
	private String issueDate;
	private String description; // Additional details about the issue

	// Constructor
	public IssueHistory(String issueType, String issueDate, String description) {
		this.issueType = issueType;
		this.issueDate = issueDate;
		this.description = description;
	}

	// Getters and setters
	public String getIssueType() {
		return issueType;
	}

	public void setIssueType(String issueType) {
		this.issueType = issueType;
	}

	public String getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "IssueHistory [issueType=" + issueType + ", issueDate=" + issueDate + ", description=" + description
				+ "]";
	}
}
