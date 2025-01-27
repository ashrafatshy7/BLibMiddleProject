package enteties;

import java.io.Serializable;
/**
 * Represents a subscriber entity in the library system.
 * Inherits from the User class and implements Serializable for object serialization.
 */
public class Subscriber extends User implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Indicates whether the subscriber is active. */
    private boolean isActive;

    /**
     * Constructs a Subscriber instance with all required details.
     *
     * @param ID The subscriber's ID.
     * @param name The subscriber's name.
     * @param phoneNumber The subscriber's phone number.
     * @param email The subscriber's email address.
     * @param password The subscriber's password.
     */
    public Subscriber(String ID, String name, String phoneNumber, String email, String password) {
        super(ID, name, phoneNumber, email, password);
    }

    /**
     * Constructs a Subscriber instance with active status.
     *
     * @param ID The subscriber's ID.
     * @param name The subscriber's name.
     * @param phoneNumber The subscriber's phone number.
     * @param email The subscriber's email address.
     * @param isActive Whether the subscriber is active.
     */
    public Subscriber(String ID, String name, String phoneNumber, String email, boolean isActive) {
        super(ID, name, phoneNumber, email);
        this.isActive = isActive;
    }

    /**
     * Constructs a Subscriber instance with an ID only.
     *
     * @param ID The subscriber's ID.
     */
    public Subscriber(String ID) {
        super(ID);
    }

    /**
     * Retrieves the active status of the subscriber.
     *
     * @return True if the subscriber is active, false otherwise.
     */
    public boolean getIsActive() {
        return isActive;
    }
}
