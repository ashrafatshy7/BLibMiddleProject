package enteties;

import java.io.Serializable;

public class Subscriber extends User implements Serializable {
    private static final long serialVersionUID = 1L;


    // Constructor
    public Subscriber(String ID, String name, String phoneNumber, String email) {
        super(ID, name, phoneNumber, email);
    }

   
}