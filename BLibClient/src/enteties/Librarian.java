package enteties;

import java.io.Serializable;
/**
 * Represents a librarian entity, which extends the User class.
 * Implements Serializable for object serialization.
 */
public class Librarian extends User implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a Librarian instance with all required details.
     *
     * @param ID The librarian's ID.
     * @param name The librarian's name.
     * @param phoneNumber The librarian's phone number.
     * @param email The librarian's email address.
     * @param password The librarian's password.
     */
    public Librarian(String ID, String name, String phoneNumber, String email, String password) {
        super(ID, name, phoneNumber, email, password);
    }

    /**
     * Constructs a Librarian instance without a password.
     *
     * @param ID The librarian's ID.
     * @param name The librarian's name.
     * @param phoneNumber The librarian's phone number.
     * @param email The librarian's email address.
     */
    public Librarian(String ID, String name, String phoneNumber, String email) {
        super(ID, name, phoneNumber, email);
    }

    /**
     * Constructs a Librarian instance with an ID only.
     *
     * @param ID The librarian's ID.
     */
    public Librarian(String ID) {
        super(ID);
    }

    /**
     * Returns a string representation of the librarian.
     *
     * @return A formatted string containing librarian details.
     */
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
