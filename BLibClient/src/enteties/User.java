package enteties;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	private String ID;
	private String name;
	private String phoneNumber;
	private String email;

	// Constructor
	public User(String ID, String name, String phoneNumber, String email) {
		this.ID = ID;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.email = email;
	}

	public User(String ID) {
		this.ID = ID;
	}

	// Getters and Setters
	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
