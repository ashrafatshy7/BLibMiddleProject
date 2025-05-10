package enteties;

import java.io.Serializable;

public class Librarian extends User implements Serializable  {
	
	private static final long serialVersionUID = 1L;

    // Constructor
    public Librarian(String ID, String name, String phoneNumber, String email) {
        super(ID, name, phoneNumber, email);
    }

    @Override
    public String toString() {
        return "Librarian{" +
                "ID='" + getID() + '\'' +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", phone='" + getPhoneNumber() + '\'' +
                '}';
    }
}