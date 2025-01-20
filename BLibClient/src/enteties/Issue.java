package enteties;

import java.io.Serializable;

public class Issue implements Serializable {
	private static final long serialVersionUID = 1L;

	private String type;
	
	public Issue(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
}
