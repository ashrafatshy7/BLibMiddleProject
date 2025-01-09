package enteties;


public class Librarian extends User {

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