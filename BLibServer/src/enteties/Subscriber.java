package enteties;

import java.io.Serializable;

public class Subscriber extends User implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private boolean isActive;

	// Constructor
	public Subscriber(String ID, String name, String phoneNumber, String email, String password) {
		super(ID, name, phoneNumber, email, password);
	}
	
	public Subscriber(String ID, String name, String phoneNumber, String email, boolean isActive) {
		super(ID, name, phoneNumber, email);
		this.isActive = isActive;
	}
	
	public Subscriber(String ID) {
		super(ID);
	}


	public boolean getIsActive() {
		return isActive;
	}
	
}