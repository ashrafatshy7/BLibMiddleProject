package enteties;

import java.io.Serializable;
/**
 * Represents the history of an issue associated with a book or user.
 * Implements Serializable for object serialization.
 */
public class IssueHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    /** The type of the issue (e.g., "Lost", "Damaged"). */
    private String issueType;

    /** The date when the issue occurred. */
    private String issueDate;

    /** A description providing more details about the issue. */
    private String issueDescription;

    /**
     * Constructs an IssueHistory object with the specified details.
     *
     * @param issueType The type of the issue.
     * @param issueDate The date the issue occurred.
     * @param issueDescription A detailed description of the issue.
     */
    public IssueHistory(String issueType, String issueDate, String issueDescription) {
        this.issueType = issueType;
        this.issueDate = issueDate;
        this.issueDescription = issueDescription;
    }

    /**
     * Retrieves the type of the issue.
     *
     * @return The issue type.
     */
    public String getIssueType() {
        return issueType;
    }

    /**
     * Retrieves the date when the issue occurred.
     *
     * @return The issue date.
     */
    public String getIssueDate() {
        return issueDate;
    }

    /**
     * Retrieves the detailed description of the issue.
     *
     * @return The issue description.
     */
    public String getIssueDescription() {
        return issueDescription;
    }
}
