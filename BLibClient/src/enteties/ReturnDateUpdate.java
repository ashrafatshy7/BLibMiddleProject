package enteties;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ReturnDateUpdate implements Serializable {
	// Make sure that all fields in the class are serializable.
	private static final long serialVersionUID = 1L;

	// Define fields and constructors as required
	private String subscriberId;
	private List<Map<String, String>> loanDetails;

	public ReturnDateUpdate(String subscriberId, List<Map<String, String>> loanDetails) {
		this.subscriberId = subscriberId;
		this.loanDetails = loanDetails;
	}

	// Getters and setters if needed
	public String getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(String subscriberId) {
		this.subscriberId = subscriberId;
	}

	public List<Map<String, String>> getLoanDetails() {
		return loanDetails;
	}

	public void setLoanDetails(List<Map<String, String>> loanDetails) {
		this.loanDetails = loanDetails;
	}
}
